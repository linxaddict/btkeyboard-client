package com.machineinsight_it.btkeyboard.ui.main

import android.Manifest
import android.app.Dialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.machineinsight_it.btkeyboard.R
import com.machineinsight_it.btkeyboard.ble.BROADCAST_EVENT_NAME
import com.machineinsight_it.btkeyboard.ble.BtKeyboardService
import com.machineinsight_it.btkeyboard.databinding.ActivityMainBinding
import com.machineinsight_it.btkeyboard.ui.base.adapter.MultiViewAdapter
import com.machineinsight_it.btkeyboard.ui.connection.ConnectionFragment
import com.machineinsight_it.btkeyboard.ui.device.DeviceViewModel
import dagger.android.AndroidInjection
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.indeterminateProgressDialog
import javax.inject.Inject

private const val PERMISSION_REQUEST_CODE = 100

class MainActivity : AppCompatActivity(), MainViewAccess {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: MainViewModel


    private lateinit var devicesAdapter: MultiViewAdapter
    private lateinit var binding: ActivityMainBinding

    private var connectingDialog: Dialog? = null
    private val connectionFragment = ConnectionFragment()

    private fun showConnection() {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(R.animator.fade_in, android.R.animator.fade_out)
        transaction.add(R.id.fragment_container, connectionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private val eventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                viewModel.handleBleEvent(BtKeyboardService.extractConnectionEvent(intent))
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    private fun acquireLocationPermissionIfNeeded() {
        if (!isLocationPermissionGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                longSnackbar(binding.root, R.string.locationPermissionExplanation)

                acquireLocationPermissionIfNeeded()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun startBluetoothScan() {
        if (isLocationPermissionGranted()) {
            viewModel.scan()
        } else {
            acquireLocationPermissionIfNeeded()
        }
    }

    private fun createDevicesAdapter() {
        devicesAdapter = MultiViewAdapter.Builder(viewModel.devicesModels)
                .register(
                        R.layout.list_item_device,
                        DeviceViewModel::class.java,
                        { model, binding ->
                            binding.root.setOnClickListener {
                                val serviceIntent = BtKeyboardService.createDeviceIntent(
                                        MainActivity@this, model.mac)
                                startService(serviceIntent)
                            }
                        }
                )
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        val receiver = LocalBroadcastManager.getInstance(this)
        receiver.registerReceiver(eventReceiver, IntentFilter(BROADCAST_EVENT_NAME))

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.model = viewModel
        binding.recyclerDevices.layoutManager = LinearLayoutManager(this)

        createDevicesAdapter()

        binding.recyclerDevices.adapter = devicesAdapter
        binding.recyclerDevices.addItemDecoration(DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL))

        binding.swipeRefresh.setOnRefreshListener { startBluetoothScan() }

        viewModel.checkConnectivity()

        startBluetoothScan()
    }

    override fun onDestroy() {
        super.onDestroy()

        val receiver = LocalBroadcastManager.getInstance(this)
        receiver.unregisterReceiver(eventReceiver)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                snackbar(binding.root, R.string.permissionGranted)
                viewModel.scan()
            } else {
                snackbar(binding.root, R.string.bluetoothDisabled)
            }
        }
    }

    override fun notifyDeviceAdded(position: Int) {
        devicesAdapter.notifyItemInserted(position)
    }

    override fun notifyDevicesRemoved(count: Int) {
        devicesAdapter.notifyItemRangeRemoved(0, count)
    }

    override fun showMessage(message: String) {
        snackbar(binding.root, message)
    }

    override fun showMessage(message: Int) {
        snackbar(binding.root, message)
    }

    override fun showConnectingDialog() {
        connectingDialog = indeterminateProgressDialog(R.string.connecting)
        connectingDialog?.setCancelable(false)
    }

    override fun hideConnectingDialog() {
        connectingDialog?.hide()
    }

    override fun showConnectionDetails() {
        showConnection()
    }
}
