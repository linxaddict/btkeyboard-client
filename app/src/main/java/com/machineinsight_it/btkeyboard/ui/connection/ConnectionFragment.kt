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
import com.machineinsight_it.btkeyboard.databinding.FragmentConnectionBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ConnectionFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: ConnectionViewModel

    lateinit var binding: FragmentConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ConnectionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connection, container, false)
        binding.model = viewModel

        return binding.root
    }
}