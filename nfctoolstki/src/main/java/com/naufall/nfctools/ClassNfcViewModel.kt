package com.naufall.nfctools

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

}