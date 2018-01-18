package com.machineinsight_it.btkeyboard.domain

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
data class Device(val mac: String, val name: String) : Parcelable