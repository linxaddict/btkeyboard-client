package com.machineinsight_it.btkeyboard.ble.event.base

import com.machineinsight_it.btkeyboard.domain.Device

interface BleEvent {
    val device: Device

    fun handleBy(handler: BleEventHandler)
}