package com.machineinsight_it.btkeyboard.ble

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.machineinsight_it.btkeyboard.R
import com.machineinsight_it.btkeyboard.ble.di.DaggerBtKeyboardServiceComponent
import com.machineinsight_it.btkeyboard.ble.event.ConnectedEvent
import com.machineinsight_it.btkeyboard.ble.event.ConnectingEvent
import com.machineinsight_it.btkeyboard.ble.event.ConnectionErrorEvent
import com.machineinsight_it.btkeyboard.ble.event.base.BleEvent
import com.machineinsight_it.btkeyboard.domain.Device
import com.machineinsight_it.btkeyboard.ui.main.MainActivity
import com.polidea.rxandroidble.RxBleClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.error
import rx.Subscription
import javax.inject.Inject

const val BROADCAST_EVENT_NAME = "btkeyboard_event_broadcast"

private const val EXTRA_STOP_SERVICE = "stopService"
private const val EXTRA_MAC = "deviceMac"
private const val EXTRA_CONNECTION_EVENT = "connectionEvent"
private const val NOTIFICATION_ID = 1234
private const val NOTIFICATION_CHANNEL_ID = "1"

class BtKeyboardService : Service(), AnkoLogger {
    companion object {
        var isRunning = false
            private set

        var connectedDevice: Device? = null
            private set

        fun createDeviceIntent(context: Context, deviceMac: String) =
                Intent(context, BtKeyboardService::class.java).apply {
                    putExtra(EXTRA_MAC, deviceMac)
                }

        fun extractConnectionEvent(intent: Intent): BleEvent {
            if (!intent.hasExtra(EXTRA_CONNECTION_EVENT)) {
                return ConnectionErrorEvent(null)
            }

            if (intent.getParcelableExtra<BleEvent>(EXTRA_CONNECTION_EVENT) == null) {
                return ConnectionErrorEvent(null)
            }

            return intent.getParcelableExtra(EXTRA_CONNECTION_EVENT) as BleEvent
        }
    }

    @Inject
    lateinit var btClient: RxBleClient

    private var connectionSubscription: Subscription? = null

    private lateinit var broadcastManager: LocalBroadcastManager

    private fun createConnectionEventIntent(event: BleEvent) =
            Intent(BROADCAST_EVENT_NAME).apply {
                putExtra(EXTRA_CONNECTION_EVENT, event)
            }

    private fun broadcastConnectionEvent(event: BleEvent) =
            broadcastManager.sendBroadcast(createConnectionEventIntent(event))

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val serviceStopIntent = Intent(this, BtKeyboardService::class.java)
        serviceStopIntent.putExtra(EXTRA_STOP_SERVICE, true)

        val pendingServiceStopIntent = PendingIntent.getService(this, 0, serviceStopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.notificationTitle))
                .setContentText(getString(R.string.notificationBody))
                .setSmallIcon(R.drawable.ic_keyboard_white_24dp)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_stop_white_24dp, getString(R.string.stopService), pendingServiceStopIntent)
                .build()
    }

    private fun connectToKeyboard(mac: String) {
        connectionSubscription?.unsubscribe()

        val bleDevice = btClient.getBleDevice(mac)
        val device = Device(bleDevice.macAddress, bleDevice.name ?: "")

        connectionSubscription = bleDevice
                .establishConnection(false)
                .doOnSubscribe { broadcastConnectionEvent(ConnectingEvent(device)) }
                .flatMap { connection -> connection.setupNotification(BtKeyboardServiceProfile.key1Characteristic) }
                .subscribe(
                        { connection ->
                            // TODO: connection.discoverServices()

                            connectedDevice = device
                            broadcastConnectionEvent(ConnectedEvent(device))

                            info("connection established")
                        },
                        { e ->
                            connectedDevice = null
                            broadcastConnectionEvent(ConnectionErrorEvent(device))

                            error("cannot establish connection: " + e)
                        }
                )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (intent.hasExtra(EXTRA_STOP_SERVICE)) {
                stopSelf()
            } else if (intent.hasExtra(EXTRA_MAC)) {
                connectToKeyboard(intent.getStringExtra(EXTRA_MAC))
            }
        }

        isRunning = true

        return START_STICKY
    }

    override fun onCreate() {
        DaggerBtKeyboardServiceComponent
                .builder()
                .service(this)
                .build()
                .inject(this)

        broadcastManager = LocalBroadcastManager.getInstance(applicationContext)

        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onDestroy() {
        super.onDestroy()

        connectionSubscription?.unsubscribe()
        connectionSubscription = null
        isRunning = false
    }
}