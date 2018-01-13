package com.machineinsight_it.btkeyboard.ble.event

import android.annotation.SuppressLint
import android.os.Parcelable
import com.machineinsight_it.btkeyboard.ble.event.base.BleEvent
import com.machineinsight_it.btkeyboard.ble.event.base.BleEventHandler
import com.machineinsight_it.btkeyboard.domain.Device
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@SuppressLint("ParcelCreator")
class ConnectingEvent(val device: @RawValue Device) : BleEvent, Parcelable {
    override fun handleBy(handler: BleEventHandler) {
        handler.handleConnecting(this)
    }
}