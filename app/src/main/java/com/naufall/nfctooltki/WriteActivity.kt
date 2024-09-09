package com.naufall.nfctooltki

import android.app.PendingIntent
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.naufall.nfctools.utils.WriteableTag

class WriteActivity : AppCompatActivity() {

    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_write)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

    }
    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("WriteActivity", "onNewIntent: $intent \n ${NfcAdapter.ACTION_TAG_DISCOVERED == intent.action}")
        Log.d("WriteActivity", "ACTION_NDEF_DISCOVERED")
        try {
            Log.d("WriteActivity", "0")
//            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            Log.d("WriteActivity", "1")
            val etData = findViewById<EditText>(R.id.et_data)
            var tag: WriteableTag? = null
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val message = etData.text.toString().toByteArray()
            try {
                tag = tagFromIntent?.let { WriteableTag(it) }
                tag?.writeData(tag!!.tagId.toString(), NdefMessage(message.toString().toByteArray()), message.toString().toByteArray())
            } catch (e: FormatException) {
                Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            }
//            val ndef = Ndef.get(tag)
//            Log.d("WriteActivity", "2")
//            ndef.connect()
//            Log.d("WriteActivity", "3")
//            ndef.writeNdefMessage(NdefMessage(message))
//            Log.d("WriteActivity", "4")
//            ndef.close()
//            Log.d("WriteActivity", "5")
            Toast.makeText(this, "Berhasil menulis - $message", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menulis", Toast.LENGTH_SHORT).show()
        }
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action || NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {

        }
    }
}