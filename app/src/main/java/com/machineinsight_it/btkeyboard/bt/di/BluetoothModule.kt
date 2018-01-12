package com.machineinsight_it.btkeyboard.bt.di

import android.content.Context
import com.polidea.rxandroidble.RxBleClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BluetoothModule {
    @Provides
    @Singleton
    fun provideRxBleClient(context: Context): RxBleClient = RxBleClient.create(context)
}