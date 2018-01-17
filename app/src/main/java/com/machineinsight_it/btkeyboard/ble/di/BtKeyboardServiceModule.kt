package com.machineinsight_it.btkeyboard.ble.di

import android.bluetooth.BluetoothGattService
import android.content.Context
import com.machineinsight_it.btkeyboard.ble.BtKeyboardGattServiceFactory
import com.machineinsight_it.btkeyboard.ble.BtKeyboardGattServiceFactoryImpl
import com.machineinsight_it.btkeyboard.ble.BtKeyboardService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BtKeyboardServiceModule {
    @Provides
    @Singleton
    fun provideContext(service: BtKeyboardService): Context {
        return service.applicationContext
    }

    @Provides
    @Singleton
    fun provideBtKeyboardGattServiceFactory(): BtKeyboardGattServiceFactory = BtKeyboardGattServiceFactoryImpl()

    @Provides
    @Singleton
    fun provideBtKeyboardGattService(factory: BtKeyboardGattServiceFactory): BluetoothGattService = factory.createGattService()
}