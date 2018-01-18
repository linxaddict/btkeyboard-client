package com.machineinsight_it.btkeyboard.ui.connection

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.machineinsight_it.btkeyboard.R
import com.machineinsight_it.btkeyboard.ble.BtKeyboardService
import com.machineinsight_it.btkeyboard.databinding.FragmentConnectionBinding
import com.machineinsight_it.btkeyboard.domain.Device
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

private const val EXTRA_DEVICE = "device"

class ConnectionFragment : Fragment(), ConnectionViewAccess {
    companion object {
        fun create(device: Device?): ConnectionFragment =
            ConnectionFragment().apply {
                arguments = Bundle().apply { putParcelable(EXTRA_DEVICE, device) }
            }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ConnectionViewModel::class.java)
    }

    private lateinit var binding: FragmentConnectionBinding

    private var device: Device? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidSupportInjection.inject(this)
        device = arguments?.getParcelable(EXTRA_DEVICE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connection, container, false)
        binding.model = viewModel
        viewModel.device = device

        return binding.root
    }

    override fun disconnectBtService() {
        context?.let {
            context?.startService(BtKeyboardService.createDisconnectIntent(it))
        }
    }
}