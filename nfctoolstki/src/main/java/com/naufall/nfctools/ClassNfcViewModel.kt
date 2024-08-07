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
        _nfcValue.value = value
    }

    private val _selectedBluetoothDeviceAddress = MutableLiveData<String>()
    val selectedBluetoothDeviceAddress: LiveData<String>
        get() = _selectedBluetoothDeviceAddress
    fun setSelectedBluetoothDeviceAddress(value: String) {
        Log.d("ClassNfcViewModel", "setSelectedBluetoothDeviceAddress: $value")
        _selectedBluetoothDeviceAddress.value = value
    }

}