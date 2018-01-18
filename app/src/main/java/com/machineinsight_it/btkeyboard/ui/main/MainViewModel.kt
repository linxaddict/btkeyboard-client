package com.machineinsight_it.btkeyboard.ui.main

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.machineinsight_it.btkeyboard.R
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
import com.polidea.rxandroidble.scan.ScanResult
import com.polidea.rxandroidble.scan.ScanSettings
import io.reactivex.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val SCAN_TIMEOUT: Long = 10 // seconds

class MainViewModel @Inject constructor(
        private val viewAccess: MainViewAccess,
        private val btClient: RxBleClient) : BaseViewModel() {

    val connectedDevice = ObservableField<Device>(null)
    val connectedToDevice = ObservableBoolean(false)
    val devicesModels = mutableListOf<DeviceViewModel>()
    val scanInProgress = ObservableBoolean(false)
    val noDevicesFoundVisible = ObservableBoolean(false)

    private var bleScanSubscription: Subscription? = null

    private val eventHandler = object : BleEventHandler {
        override fun handleConnecting(event: ConnectingEvent) {
            viewAccess.showConnectingDialog()
        }

        override fun handleConnected(event: ConnectedEvent) {
            viewAccess.hideConnectingDialog()
            viewAccess.showConnectionDetails()
        }

        override fun handleConnectionError(event: ConnectionErrorEvent) {
            viewAccess.hideConnectingDialog()
            viewAccess.showMessage(R.string.connectionError)
        }

        override fun handleDisconnected(event: DisconnectedEvent) {
            viewAccess.hideConnectingDialog()
            viewAccess.showMessage(R.string.disconnect)
        }

        override fun handle(event: BleEvent) {
            event.handleBy(this)
        }
    }

    private val addedDevices = mutableSetOf<String>()

    private fun addNewDevice(device: Device): Boolean {
        if (!addedDevices.contains(device.mac)) {
            addedDevices.add(device.mac)
            devicesModels.add(DeviceViewModel(device.name, device.mac))

            return true
        }

        return false
    }

    private fun cancelScanningAfterTimeLimitExceeded() {
        Observable.timer(SCAN_TIMEOUT, TimeUnit.SECONDS).subscribe {
            bleScanSubscription?.unsubscribe()

            scanInProgress.set(false)
            noDevicesFoundVisible.set(devicesModels.isEmpty())
        }
    }

    private fun startScanning() {
        bleScanSubscription?.let {
            it.unsubscribe()
            viewAccess.notifyDataSetChanged()
        }

        bleScanSubscription?.unsubscribe()
        bleScanSubscription = btClient.scanBleDevices(ScanSettings.Builder().build())
                .subscribe(
                        { result -> handleNewDeviceDiscovered(result) },
                        { throwable -> error("Bluetooth error: " + throwable) }
                )
    }

    private fun handleNewDeviceDiscovered(result: ScanResult) {
        val added = addNewDevice(device = Device(result.bleDevice.macAddress,
                result.bleDevice.name ?: ""))
        if (added) {
            viewAccess.notifyDeviceAdded(devicesModels.size - 1)
        }
    }

    private fun prepareViewForNewScan() {
        scanInProgress.set(true)
        noDevicesFoundVisible.set(false)
    }

    private fun clearCachedDevices() {
        addedDevices.clear()
        devicesModels.clear()

        viewAccess.notifyDevicesRemoved(addedDevices.size)
    }

    fun scan() {
        clearCachedDevices()
        prepareViewForNewScan()

        startScanning()

        cancelScanningAfterTimeLimitExceeded()
    }

    fun checkConnectivity() {
        connectedDevice.set(BtKeyboardService.connectedDevice)
        connectedToDevice.set(connectedDevice.get() != null)
    }

    fun handleBleEvent(event: BleEvent) = event.handleBy(eventHandler)
}