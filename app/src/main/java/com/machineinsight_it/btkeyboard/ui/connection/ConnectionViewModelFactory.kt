package com.machineinsight_it.btkeyboard.ui.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.machineinsight_it.btkeyboard.ui.connection.ConnectionViewModel
import javax.inject.Inject

class ConnectionViewModelFactory @Inject constructor(private val viewModel: ConnectionViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConnectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        } else {
            throw IllegalArgumentException("Unknown class name")
        }
    }
}