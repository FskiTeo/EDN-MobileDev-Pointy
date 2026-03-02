package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScanViewModel : ViewModel() {
    private val _lastScannedUid = MutableStateFlow<String?>(null)
    val lastScannedUid: StateFlow<String?> = _lastScannedUid

    fun onCardScanned(uid: String) {
        _lastScannedUid.value = uid
    }

    fun resetScan() {
        _lastScannedUid.value = null
    }
}