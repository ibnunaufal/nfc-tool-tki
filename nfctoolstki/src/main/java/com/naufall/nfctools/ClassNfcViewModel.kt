package com.naufall.nfctools

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClassNfcViewModel: ViewModel() {

    private val _nfcValue = MutableLiveData<String>()
    /**
     * Variable to observe NFC value from ViewModel
     */
    val nfcValue: LiveData<String>
        get() = _nfcValue

    /**
     * Set NFC value to ViewModel, then it will be observed by the activity
     */
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

    private val _readableString = MutableLiveData<String>()
    /**
     * Variable to observe readable string from ViewModel
     */
    val readableString: LiveData<String>
        get() = _readableString

    /**
     * Set readable string to ViewModel, then it will be observed by the activity
     */
    fun setReadableString(value: String) {
        _readableString.value = value
    }

    private val _stringToWrite = MutableLiveData<String>()
    /**
     * Variable to observe string to write from ViewModel
     */
    val stringToWrite: LiveData<String>
        get() = _stringToWrite

    /**
     * Set string to write into ViewModel, then it will be observed by the activity
     */
    fun setStringToWrite(value: String) {
        setIsOnWritingProcess(true)
        _stringToWrite.value = value
    }

    private val _isOnWritingProcess = MutableLiveData<Boolean>()
    /**
     * Variable to observe is now on writing process or not from ViewModel
     */
    val isOnWritingProcess: LiveData<Boolean>
        get() = _isOnWritingProcess
    /**
     * Set is now on writing process or not into ViewModel, then it will be observed by the activity
     */
    fun setIsOnWritingProcess(value: Boolean) {
        _isOnWritingProcess.value = value
    }



    private val _selectedBluetoothDeviceAddress = MutableLiveData<String>()
    /**
     * Variable to observe selected Bluetooth device address from ViewModel
     */
    val selectedBluetoothDeviceAddress: LiveData<String>
        get() = _selectedBluetoothDeviceAddress
    /**
     * Set selected Bluetooth device address to ViewModel, then it will be observed by the activity
     */
    fun setSelectedBluetoothDeviceAddress(value: String) {
        Log.d("ClassNfcViewModel", "setSelectedBluetoothDeviceAddress: $value")
        _selectedBluetoothDeviceAddress.value = value
    }

    private val _isConnectingBluetoothDevice = MutableLiveData<Boolean>()
    val isConnectingBluetoothDevice: LiveData<Boolean>
        get() = _isConnectingBluetoothDevice
    /**
     * Define the current device is connecting to the Bluetooth device or not
     */
    fun setIsConnectingBluetoothDevice(value: Boolean) {
        _isConnectingBluetoothDevice.value = value
    }
}