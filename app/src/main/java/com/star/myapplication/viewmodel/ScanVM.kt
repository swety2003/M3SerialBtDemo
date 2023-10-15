package com.star.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.star.myapplication.utils.BtHelper

class ScanVM: ViewModel() {
    companion object {
        private const val TAG = "ScanVM"

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ScanVM()
        }
    }


}