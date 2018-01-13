package com.machineinsight_it.btkeyboard.ble.event.base

import android.os.Parcelable

interface BleEvent : Parcelable {
    fun handleBy(handler: BleEventHandler)
}