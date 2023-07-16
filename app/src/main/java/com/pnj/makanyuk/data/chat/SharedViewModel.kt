package com.pnj.makanyuk.data.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val username = MutableLiveData<String>()
}
