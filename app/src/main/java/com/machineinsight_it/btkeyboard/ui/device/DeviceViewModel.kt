package com.machineinsight_it.btkeyboard.ui.device

import android.annotation.SuppressLint
import android.os.Parcelable
import com.machineinsight_it.btkeyboard.ui.base.model.BaseViewModel
import com.machineinsight_it.btkeyboard.ui.base.model.RowViewModel
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
class DeviceViewModel(val name: String, val mac: String) : BaseViewModel(), RowViewModel, Parcelable