package com.machineinsight_it.btkeyboard.bt.di

import android.content.Context
import com.machineinsight_it.btkeyboard.bt.BtKeyboardService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule {
    @Provides
    @Singleton
    fun provideContext(service: BtKeyboardService): Context {
        return service.applicationContext
    }
}