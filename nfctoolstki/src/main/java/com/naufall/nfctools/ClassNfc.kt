package com.naufall.nfctools

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.device.PiccManager
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.naufall.nfctools.utils.ByteUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class ClassNfc: AppCompatActivity() {
    private var adapter: NfcAdapter? = null

    var isUrovoDevice = false
    private val TAG = "PiccCheck"
    private val MSG_FOUND_UID = 12
    private var piccReader: PiccManager? = null
    private var handler: Handler? = null
    private var exec: ExecutorService? = null
    var scan_card = -1
    var SNLen = -1
    lateinit var repeatCardReader: Job

    lateinit var classNfcViewModel: ClassNfcViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNfcAdapter()

        classNfcViewModel = ClassNfcViewModel()

    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
        isUrovoDevice = Build.BRAND.contains("urovo", ignoreCase = true)
        if (isUrovoDevice){
            initializeUrovo()
        }
    }

    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
        try {
            if (isUrovoDevice){
                repeatCardReader.cancel()
                exec!!.shutdown()
            }
        } catch (e: Exception) {
            Log.e("urovo", "error", e)
        }
    }
    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE)
            adapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("ClassNfc", "Error enabling NFC foreground dispatch", ex)
        }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            adapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e("ClassNfc", "Error disabling NFC foreground dispatch", ex)
        }
    }

    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        adapter = nfcManager.defaultAdapter
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val nfcValue = NfcTools.getValidNfcFromIntent(intent)
        classNfcViewModel.setNfcValue(nfcValue)
    }

    private fun initializeUrovo() {
        Log.d("urovo", "preparing")
        exec = Executors.newSingleThreadExecutor()
        piccReader = PiccManager()

        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_FOUND_UID -> {
                        val uid = msg.obj as String
                        Log.d("hasil", "hex: $uid")
                        // call nfc read
                        if (!exec!!.isShutdown){
                            repeatCardReader.cancel()
                            classNfcViewModel.setNfcValue(uid)
                            // always set to livedata, in case has same card,
                            // handle it on activity/fragment levels
                        }
                    }
                    else -> {}
                }
                super.handleMessage(msg)
            }
        }
        Log.d("urovo", "preparing2")
        urovoReaderPreparing()
        repeatCardReader = repeatFun()
    }
    private fun repeatFun(): Job {
        return lifecycleScope.launch(Dispatchers.IO){
            while (isActive) {
                urovoChecking()
                delay(500)
            }
        }
    }
    @Override
    private fun urovoChecking(){
        if (exec!!.isShutdown){
            return
        }
        exec!!.execute(Thread({
            val CardType = ByteArray(2)
            val Atq = ByteArray(14)
            val SAK = 1.toChar()
            val sak = ByteArray(1)
            sak[0] = SAK.code.toByte()
            val SN = ByteArray(10)
            scan_card = piccReader!!.request(CardType, Atq)
            if (scan_card > 0) {
                SNLen = piccReader!!.antisel(SN, sak)
                Log.d(TAG, "SNLen = $SNLen")
                val msg = handler!!.obtainMessage(MSG_FOUND_UID)
                msg.obj = bytesToHexStringUrovo(SN)

                (handler as Handler).sendMessage(msg)
            }
        }, "picc check"))
    }
    @Override
    private fun urovoReaderPreparing(){
        exec!!.execute(Thread({
            piccReader!!.open()
        }, "picc open"))
    }
    private fun bytesToHexStringUrovo(src: ByteArray): String? {
        if (ByteUtils.isNullOrEmpty(src)) {
            return null
        }
        val sb = StringBuilder()
        for (b in src) {
            sb.append(String.format("%02X", b))
        }
        val temp = sb.toString().substring(0, 8)
        val returnedValue = balikUrovo(temp).toLong(16)

        return returnedValue.toString()
    }

    private fun balikUrovo(str: String) : String{
        var buffer = ""
        var nfcid = ""
        for (index in str.length - 1 downTo 1 step 2) {
            buffer += str[index - 1]
            buffer += str[index] + ""
        }
        buffer = buffer.trim()
        buffer = buffer.takeLast(8)
        nfcid = buffer
        return  nfcid
    }

}