package com.naufall.nfctooltki

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.naufall.nfctools.utils.WriteableTag

class ReadActivity : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_read)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

    }

//    override fun onResume() {
//        super.onResume()
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
//            PendingIntent.FLAG_IMMUTABLE
//        )
//        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
//    }

    override fun onResume() {
        super.onResume()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        val intentFilters = arrayOf<IntentFilter>(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        )
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null)
    }

    override fun onPause() {
        super.onPause()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter.disableForegroundDispatch(this)
    }

    // Here comes the moment of truth!
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        try {
            val tvData = findViewById<TextView>(R.id.tv_data)
//            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val etData = findViewById<EditText>(R.id.et_data)
            var tag: WriteableTag? = null
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)

            try {
                tag = tagFromIntent?.let { WriteableTag(it) }


            } catch (e: FormatException) {
                Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            }

            if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
                val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
                } else {
                    intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
                }
                tag?.id?.let {
                    val tagValue = it.toHexString()
                    tvData.text = tagValue
                    Toast.makeText(this, "NFC tag detected: $tagValue", Toast.LENGTH_SHORT).show()
                }
            }
//            val ndef = Ndef.get(tag)
//            ndef.connect()
//            val message = ndef.ndefMessage
//            ndef.close()

//            Log.d("ReadActivity", ndef.toString())
//            Log.d("ReadActivity", ndef.ndefMessage.toString())

//            tvData.text = String(message.records[0].payload)
            if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
                // Ta-da! Handle NFC data here

            }
        } catch (e: Exception) {
            Log.e("ReadActivity", "Error reading NFC tag", e)
        }
    }

    fun ByteArray.toHexString(): String {
        val hexChars = "0123456789ABCDEF"
        val result = StringBuilder(size * 2)

        map { byte ->
            val value = byte.toInt()
            val hexChar1 = hexChars[value shr 4 and 0x0F]
            val hexChar2 = hexChars[value and 0x0F]
            result.append(hexChar1)
            result.append(hexChar2)
        }

        return result.toString()
    }


    private fun createNFCIntentFilter(): Array<IntentFilter> {
        val intentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        try {
            intentFilter.addDataType("*/*")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("Failed to add MIME type.", e)
        }
        return arrayOf(intentFilter)
    }
}