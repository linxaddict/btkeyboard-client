package com.machineinsight_it.btkeyboard.ble.event.base

import com.machineinsight_it.btkeyboard.ble.event.ConnectedEvent
import com.machineinsight_it.btkeyboard.ble.event.ConnectingEvent
import com.machineinsight_it.btkeyboard.ble.event.ConnectionErrorEvent
import com.machineinsight_it.btkeyboard.ble.event.DisconnectedEvent

interface BleEventHandler {
    fun handleConnecting(event: ConnectingEvent)

    fun handleConnected(event: ConnectedEvent)

    fun handleConnectionError(event: ConnectionErrorEvent)

    fun handleDisconnected(event: DisconnectedEvent)

    fun handle(event: BleEvent)
}