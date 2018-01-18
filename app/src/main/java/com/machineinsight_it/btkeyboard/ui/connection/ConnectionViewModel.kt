package com.machineinsight_it.btkeyboard.ui.connection

import com.machineinsight_it.btkeyboard.domain.Device
import com.machineinsight_it.btkeyboard.ui.base.model.BaseViewModel
import javax.inject.Inject

class ConnectionViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var connectionViewAccess: ConnectionViewAccess

    var device: Device? = null

    fun disconnect() = connectionViewAccess.disconnectBtService()
}