package com.machineinsight_it.btkeyboard.bt

import android.content.Context
import com.machineinsight_it.btkeyboard.bt.BtKeyboardService
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