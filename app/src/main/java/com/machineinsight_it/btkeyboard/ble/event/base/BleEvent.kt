package com.machineinsight_it.btkeyboard.ble.event.base

import android.os.Parcelable
import com.machineinsight_it.btkeyboard.domain.Device

interface BleEvent : Parcelable {
    val device: Device?

    fun handleBy(handler: BleEventHandler)
}