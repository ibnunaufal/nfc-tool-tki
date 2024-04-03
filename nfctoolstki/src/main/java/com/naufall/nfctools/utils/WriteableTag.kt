package com.naufall.nfctools.utils

import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.util.Log
import java.io.IOException
import java.util.ArrayList
import java.util.Arrays

class WriteableTag @Throws(FormatException::class) constructor(tag: Tag) {
    private val NFC_A = NfcA::class.java.canonicalName
    private val NDEF = Ndef::class.java.canonicalName
    private val NDEF_FORMATABLE = NdefFormatable::class.java.canonicalName
    private val ISO_DEP = IsoDep::class.java.canonicalName

    private var nfcA: NfcA? = null
    private val ndef: Ndef?
    private var ndefFormatable: NdefFormatable? = null
    private var isoDep: IsoDep? = null

    val tagId: String?
        get() {
            if (ndef != null) {
                val idnfc = ndef.tag.id

                return bytesToHexString(idnfc)
            } else if (ndefFormatable != null) {
                return bytesToHexString(ndefFormatable!!.tag.id)
            }else if (nfcA != null){
                val idnfc_a = nfcA!!.tag.id
                return bytesToHexString(nfcA!!.tag.id)
            }
            return null
        }
    val isIsoDep: Boolean
        get() = isoDep != null

    val tagTechList: List<String>
        get() {
            val techList = ArrayList<String>()
            if (ndef != null) {
                techList.add(NDEF)
            }
            if (ndefFormatable != null) {
                techList.add(NDEF_FORMATABLE)
            }
            if (isoDep != null) {
                techList.add(ISO_DEP)
            }
            if (nfcA != null){
                techList.add(NFC_A)
            }
            return techList
        }
    init {

        val technologies = tag.techList
        val tagTechs = Arrays.asList(*technologies)
        Log.d("nfc", "techs: $tagTechs")
        if (tagTechs.contains(ISO_DEP)){
            isoDep = IsoDep.get(tag)
            Log.d("WritableTag", "contains iso_dep \n $isoDep")
        }
        if (tagTechs.contains(NDEF)) {
            Log.i("WritableTag", "contains ndef")
            ndef = Ndef.get(tag)
            ndefFormatable = null
        } else if (tagTechs.contains(NDEF_FORMATABLE)) {
            Log.i("WritableTag", "contains ndef_formatable")
            ndefFormatable = NdefFormatable.get(tag)
            ndef = null
        } else if (tagTechs.contains(NFC_A)){
            nfcA = NfcA.get(tag)
            ndef = null
        }
        else {
            throw FormatException("Tag doesn't support ndef")

        }
    }

    @Throws(IOException::class, FormatException::class)
    fun writeData(
        tagId: String,
        message: NdefMessage,
        messageA : ByteArray
    ): Boolean {
        if (tagId != tagId) {
            return false
        }
        if (ndef != null) {
            ndef.connect()
            if (ndef.isConnected) {
                ndef.writeNdefMessage(message)
                return true
            }
        } else if (ndefFormatable != null) {
            ndefFormatable!!.connect()
            if (ndefFormatable!!.isConnected) {
                ndefFormatable!!.format(message)
                return true
            }
        }
        else if (nfcA != null){
            nfcA!!.connect()
            if (nfcA!!.isConnected) {
                nfcA!!.transceive(messageA)
                return true
            }

        }
        return false
    }
    @Throws(IOException::class)
    private fun close() {
        ndef?.close() ?: ndefFormatable?.close()
    }
    companion object {
        fun bytesToHexString(src: ByteArray): String? {
            if (ByteUtils.isNullOrEmpty(src)) {
                return null
            }
            val sb = StringBuilder()
            for (b in src) {
                sb.append(String.format("%02X", b))
            }
            return sb.toString()

        }
    }
}