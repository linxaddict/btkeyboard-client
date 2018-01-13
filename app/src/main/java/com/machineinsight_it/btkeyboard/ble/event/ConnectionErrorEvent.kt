package com.machineinsight_it.btkeyboard.ble.event

import android.annotation.SuppressLint
import android.os.Parcelable
import com.machineinsight_it.btkeyboard.ble.event.base.BleEvent
import com.machineinsight_it.btkeyboard.ble.event.base.BleEventHandler
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
class ConnectionErrorEvent : BleEvent, Parcelable {
    override fun handleBy(handler: BleEventHandler) {
        handler.handleConnectionError(this)
    }
}