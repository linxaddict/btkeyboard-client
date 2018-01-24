package com.machineinsight_it.btkeyboard.ui.main

interface MainViewAccess {
    fun notifyDeviceAdded(position: Int)

    fun notifyDevicesRemoved(count: Int)

    fun notifyDataSetChanged()

    fun showMessage(message: String)

    fun showMessage(message: Int)

//    fun showConnectingDialog()

    fun hideConnectingDialog()

    fun showConnectionDetails()
}