package com.machineinsight_it.btkeyboard.ui.connection

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.machineinsight_it.btkeyboard.domain.Device
import com.machineinsight_it.btkeyboard.ui.base.model.BaseViewModel
import javax.inject.Inject

class ConnectionViewModel @Inject constructor() : BaseViewModel() {
    val connectedDevice = ObservableField<Device>(null)
    val connectedToDevice = ObservableBoolean(false)
}