package com.naufall.nfctools

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClassNfcViewModel: ViewModel() {

    private val _nfcValue = MutableLiveData<String>()
    val nfcValue: LiveData<String>
        get() = _nfcValue

    fun setNfcValue(value: String) {
        var nfc = value.replace("\n", "").trim()
        if (nfc.length < 10) {
            val length = 10 - nfc.length
            var newId = ""
            for (i in 1..length) {
                newId += "0"
            }
            nfc = newId + nfc
        }
        _nfcValue.value = nfc
    }

    private val _selectedBluetoothDeviceAddress = MutableLiveData<String>()
    val selectedBluetoothDeviceAddress: LiveData<String>
        get() = _selectedBluetoothDeviceAddress
    fun setSelectedBluetoothDeviceAddress(value: String) {
        Log.d("ClassNfcViewModel", "setSelectedBluetoothDeviceAddress: $value")
        _selectedBluetoothDeviceAddress.value = value
    }

    private val _isConnectingBluetoothDevice = MutableLiveData<Boolean>()
    val isConnectingBluetoothDevice: LiveData<Boolean>
        get() = _isConnectingBluetoothDevice
    fun setIsConnectingBluetoothDevice(value: Boolean) {
        _isConnectingBluetoothDevice.value = value
    }
}