package com.machineinsight_it.btkeyboard.ble

import android.content.Context
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
}