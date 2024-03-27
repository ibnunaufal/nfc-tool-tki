package com.naufall.nfctools

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import com.naufall.nfctools.utils.WriteableTag


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