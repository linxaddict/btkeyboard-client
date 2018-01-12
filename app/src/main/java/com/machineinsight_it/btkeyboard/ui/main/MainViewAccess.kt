package com.machineinsight_it.btkeyboard.ui.main

interface MainViewAccess {
    fun notifyDeviceAdded(position: Int)

    fun notifyDevicesRemoved(count: Int)
}