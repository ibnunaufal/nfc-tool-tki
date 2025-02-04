package com.naufall.nfctools

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import com.naufall.nfctools.utils.WriteableTag
import java.nio.charset.Charset


object NfcTools {

    /**
     * Enable NFC foreground dispatch.
     * This is MUST to called on onResume() of activity
     */
    fun enableNfcForegroundDispatch(currentActivity: Activity, adapter: NfcAdapter?) {
        try {
            val intent = Intent(currentActivity, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(
                currentActivity, 0, intent,
                PendingIntent.FLAG_MUTABLE
            )
            adapter?.enableForegroundDispatch(currentActivity, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("enableNfcForegroundDispatch", "Error enabling NFC foreground dispatch", ex)
        }
    }

    /**
     * Disable NFC foreground dispatch.
     * This is MUST to called on onPause() of activity
     */
    fun disableNfcForegroundDispatch(currentActivity: Activity, adapter: NfcAdapter?) {
        try {
            adapter?.disableForegroundDispatch(currentActivity)
        } catch (ex: IllegalStateException) {
            Log.e("disableNfcForegroundDispatch", "Error disabling NFC foreground dispatch", ex)
        }
    }

    /**
     * Get valid NFC from intent, if the tag is IsoDep, get the first 8 number, else get the last 8 number
     */
    fun getValidNfcFromIntent(intent: Intent): String {
        return if (getTagTechList(intent).contains("android.nfc.tech.IsoDep")) {
            getNfcFirstEightFromIntent(intent)
        } else {
            getNfcLastEightFromIntent(intent)
        }
    }

    /**
     * Get tag tech list of nfc tag from intent
     */
    fun getTagTechList(intent: Intent): List<String> {
        var tag: WriteableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WriteableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return emptyList()
        }
        return tag!!.tagTechList
    }

    /**
     * Get first eight number from nfc tag id
     */
    fun getNfcFirstEightFromIntent(intent: Intent): String {
        var tag: WriteableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WriteableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
        tagId = tag!!.tagId
        Log.d("nfc", "Activity.getNfcFirstEightFromIntent $tagId")

        var str = tagId.toString()
        var nfcid = ""
        var buffer = ""

        for (index in str.length - 1 downTo 1 step 2) {
            buffer += str[index - 1]
            buffer += str[index] + ""
        }

        buffer = buffer.trim()
        Log.d("nfc", "takeFirstEightNumber from $str to $buffer")
        buffer = buffer.take(8)
        Log.d("nfc", "takeFirstEightNumber takeFirst $buffer")

        nfcid = buffer.toLong(16).toString()

        if (nfcid.length < 10) {
            val length = 10 - nfcid.length
            var newId = ""
            for (i in 1..length) {
                newId += "0"
            }
            nfcid = newId + nfcid
        }
        Log.d("nfc", "takeFirstEightNumber " + nfcid)
        return nfcid
    }

    /**
     * Write hex string to nfc tag
     * */
    fun writeHexToNfcTag(intent: Intent, hexString: String): Boolean? {
        var tag: WriteableTag? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WriteableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return false
        }
        // Convert "Hello" to NdefMessage
        val ndefRecord = NdefRecord.createTextRecord(null, hexString)
        val ndefMessage = NdefMessage(arrayOf(ndefRecord))

        // Convert "Hello" to ByteArray
        val messageA = hexString.toByteArray(Charset.forName("UTF-8"))

        return tag?.tagId?.let { tag.writeData(it, ndefMessage, messageA) }
    }

    /**
     * Get readable string from nfc tag
     */
    fun getReadableString(intent: Intent): String {
        var readableString = ""
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            val ndef = Ndef.get(tagFromIntent)
            if (ndef != null) {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                ndef.close()
                if (ndefMessage != null) {
                    for (ndefRecord in ndefMessage.records) {
                        Log.d("nfc", "getReadableString: " + ndefRecord.toUri())
                    }
                    val firstNdefRecord = ndefMessage.records[0]
                    readableString = firstNdefRecord.payload.toString(Charset.forName("UTF-8"))
                }
            }
            return readableString
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        } catch (e: Exception) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
    }

    /**
     * Get last eight number from nfc tag id
     */
    fun getNfcLastEightFromIntent(intent: Intent): String {
        var tag: WriteableTag? = null
        var tagId: String? = null
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            tag = tagFromIntent?.let { WriteableTag(it) }
        } catch (e: FormatException) {
            Log.e("handleOnNewIntentNfc", "Unsupported tag tapped", e)
            return ""
        }
        tagId = tag!!.tagId
        Log.d("nfc", "Activity.getNfcLastEightFromIntent" + tagId.toString())

        var str = tagId.toString()
        var nfcid = ""
        var buffer = ""

        for (index in str.length - 1 downTo 1 step 2) {
            buffer += str[index - 1]
            buffer += str[index] + ""
        }
        buffer = buffer.trim()
        Log.d("nfc", "takeLastEightNumber from $str to $buffer")
        buffer = buffer.takeLast(8)
        Log.d("nfc", "takeLastEightNumber takeLast $buffer")

        nfcid = buffer.toLong(16).toString()

        if (nfcid.length < 10) {
            val length = 10 - nfcid.length
            var newId = ""
            for (i in 1..length) {
                newId += "0"
            }
            nfcid = newId + nfcid
        }
        Log.d("nfc", "takeLastEightNumber " + nfcid)
        return nfcid
    }

}