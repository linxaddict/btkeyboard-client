package com.machineinsight_it.btkeyboard.bt

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.machineinsight_it.btkeyboard.R
import com.machineinsight_it.btkeyboard.bt.di.DaggerServiceComponent
import com.machineinsight_it.btkeyboard.domain.Device
import com.machineinsight_it.btkeyboard.ui.main.MainActivity
import com.polidea.rxandroidble.RxBleClient
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import rx.Subscription
import javax.inject.Inject

const val BROADCAST_EVENT_NAME = "btkeyboard_event_broadcast"

private const val EXTRA_STOP_SERVICE = "stopService"
private const val EXTRA_MAC = "deviceMac"
private const val EXTRA_CONNECTION_STATE = "connectionState"
private const val NOTIFICATION_ID = 1234
private const val NOTIFICATION_CHANNEL_ID = "1"

class BtKeyboardService : Service(), AnkoLogger {
    enum class ConnectionState {
        CONNECTING,
        CONNECTED,
        CONNECTION_ERROR,
        DISCONNECTED,
        UNKNOWN
    }

    companion object {
        var isRunning = false
            private set

        var connectedDevice: Device? = null
            private set

        fun createDeviceIntent(context: Context, deviceMac: String) =
                Intent(context, BtKeyboardService::class.java).apply {
                    putExtra(EXTRA_MAC, deviceMac)
                }

        fun extractConnectionState(intent: Intent): ConnectionState {
            if (!intent.hasExtra(EXTRA_CONNECTION_STATE)) {
                return ConnectionState.UNKNOWN
            }

            if (intent.getSerializableExtra(EXTRA_CONNECTION_STATE) !is ConnectionState) {
                return ConnectionState.UNKNOWN
            }

            return intent.getSerializableExtra(EXTRA_CONNECTION_STATE) as ConnectionState
        }
    }

    @Inject
    lateinit var btClient: RxBleClient

    private var connectionSubscription: Subscription? = null

    private lateinit var broadcastManager: LocalBroadcastManager

    private fun createConnectionStateIntent(state: ConnectionState) =
            Intent(BROADCAST_EVENT_NAME).apply {
                putExtra(EXTRA_CONNECTION_STATE, state)
            }

    private fun broadcastConnectionState(state: ConnectionState) =
            broadcastManager.sendBroadcast(createConnectionStateIntent(state))

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
//        connectionSubscription = bleDevice
//                .establishConnection(false)
//                .doOnSubscribe { broadcastConnectionState(ConnectionState.CONNECTING) }
//                .subscribe(
//                        { connection ->
//                            info("connection established")
//                            // TODO: connection.discoverServices()
//
//                            connectedDevice = Device(mac, bleDevice.name ?: "")
//
//                            broadcastConnectionState(ConnectionState.CONNECTED)
//                        },
//                        { e ->
//                            error("cannot establish connection: " + e)
//                            connectedDevice = null
//
//                            broadcastConnectionState(ConnectionState.CONNECTION_ERROR)
//                            broadcastConnectionState(ConnectionState.DISCONNECTED)
//                        }
//                )

        broadcastConnectionState(ConnectionState.CONNECTED)
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
        DaggerServiceComponent
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