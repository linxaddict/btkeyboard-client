package com.machineinsight_it.btkeyboard.ui.main

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.machineinsight_it.btkeyboard.ble.BtKeyboardService
import com.machineinsight_it.btkeyboard.ble.event.ConnectedEvent
import com.machineinsight_it.btkeyboard.ble.event.ConnectingEvent
import com.machineinsight_it.btkeyboard.ble.event.ConnectionErrorEvent
import com.machineinsight_it.btkeyboard.ble.event.DisconnectedEvent
import com.machineinsight_it.btkeyboard.ble.event.base.BleEvent
import com.machineinsight_it.btkeyboard.ble.event.base.BleEventHandler
import com.machineinsight_it.btkeyboard.domain.Device
import com.machineinsight_it.btkeyboard.ui.base.model.BaseViewModel
import com.machineinsight_it.btkeyboard.ui.device.DeviceViewModel
import com.polidea.rxandroidble.RxBleClient
import com.polidea.rxandroidble.internal.RxBleLog
import com.polidea.rxandroidble.scan.ScanSettings
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val SCAN_TIMEOUT: Long = 5 // seconds

class MainViewModel @Inject constructor() : BaseViewModel() {
    val connectedDevice = ObservableField<Device>(null)
    val connectedToDevice = ObservableBoolean(false)
    val devicesModels = mutableListOf<DeviceViewModel>()
    val scanInProgress = ObservableBoolean(false)
    val noDevicesFoundVisible = ObservableBoolean(false)

    private val eventHandler = object : BleEventHandler {
        override fun handleConnecting(event: ConnectingEvent) {
            System.out.println("handle connecting event")
        }

        override fun handleConnected(event: ConnectedEvent) {
            System.out.println("handle connected event")
        }

        override fun handleConnectionError(event: ConnectionErrorEvent) {
            System.out.println("handle connection error event")
        }

        override fun handleDisconnected(event: DisconnectedEvent) {
            System.out.println("handle disconnected event")
        }

        override fun handle(event: BleEvent) {
            event.handleBy(this)
        }
    }

    @Inject
    lateinit var viewAccess: MainViewAccess

    @Inject
    lateinit var btClient: RxBleClient

    private val addedDevices = mutableSetOf<String>()

    private fun addNewDevice(device: Device): Boolean {
        if (!addedDevices.contains(device.mac)) {
            addedDevices.add(device.mac)
            devicesModels.add(DeviceViewModel(device.name, device.mac))

            return true
        }

        return false
    }

    fun scan() {
        RxBleLog.setLogLevel(RxBleLog.VERBOSE)

        val devicesCount = addedDevices.size

        addedDevices.clear()
        devicesModels.clear()

        viewAccess.notifyDevicesRemoved(devicesCount)

        scanInProgress.set(true)
        noDevicesFoundVisible.set(false)

        val bleScanSubscription = btClient.scanBleDevices(ScanSettings.Builder().build())
                .subscribe(
                        { result ->
                            val added = addNewDevice(device = Device(result.bleDevice.macAddress,
                                    result.bleDevice.name ?: ""))
                            if (added) {
                                viewAccess.notifyDeviceAdded(devicesModels.size - 1)
                            }
                        },
                        { throwable ->
                            error("Bluetooth error: " + throwable)
                        }
                )

        Observable.timer(SCAN_TIMEOUT, TimeUnit.SECONDS).subscribe {
            bleScanSubscription?.unsubscribe()

            scanInProgress.set(false)
            noDevicesFoundVisible.set(devicesModels.isEmpty())
        }
    }

    fun checkConnectivity() {
        connectedDevice.set(BtKeyboardService.connectedDevice)
        connectedToDevice.set(connectedDevice.get() != null)
    }

    fun handleBleEvent(event: BleEvent) = event.handleBy(eventHandler)
}