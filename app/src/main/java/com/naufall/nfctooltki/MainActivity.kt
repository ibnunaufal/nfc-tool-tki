package com.naufall.nfctooltki

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.naufall.nfctools.ClassNfc
import com.naufall.nfctools.NfcTools

class MainActivity : ClassNfc() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val write = findViewById<Button>(R.id.btn_write_data)
        val read = findViewById<Button>(R.id.btn_read_data)

        write.setOnClickListener {
            startActivity(Intent(this, WriteActivity::class.java))
        }
        read.setOnClickListener {
            startActivity(Intent(this, ReadActivity::class.java))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tvTechList = findViewById<TextView>(R.id.tv_tech_list)
        val tvSerialOriginal = findViewById<TextView>(R.id.tv_serial_original)
        val tvSerialFlipped = findViewById<TextView>(R.id.tv_serial_flipped)
        val tvNfcFirstEight = findViewById<TextView>(R.id.tv_nfc_first_8)
        val tvNfcLastEight = findViewById<TextView>(R.id.tv_nfc_last_8)
        val tvFinalResult = findViewById<TextView>(R.id.tv_final_result)
        // NfcTools.getTagTechList(intent).toString()
        tvTechList.text = NfcTools.getTagTechList(intent).toString()
        tvSerialOriginal.text = NfcTools.getNfcSerialOriginalFromIntent(intent)
        tvSerialFlipped.text = NfcTools.getNfcSerialFlippedFromIntent(intent)
        tvNfcFirstEight.text = NfcTools.getNfcFirstEightFromIntent(intent)
        tvNfcLastEight.text = NfcTools.getNfcLastEightFromIntent(intent)
        tvFinalResult.text = NfcTools.getValidNfcFromIntent(intent)

    }
}