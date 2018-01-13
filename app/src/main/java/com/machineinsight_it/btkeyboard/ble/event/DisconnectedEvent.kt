package com.machineinsight_it.btkeyboard.ble.event

import com.machineinsight_it.btkeyboard.ble.event.base.BleEvent
import com.machineinsight_it.btkeyboard.ble.event.base.BleEventHandler
import com.machineinsight_it.btkeyboard.domain.Device

class DisconnectedEvent(override val device: Device) : BleEvent {
    override fun handleBy(handler: BleEventHandler) {
        handler.handleDisconnected(this)
    }
}