package com.machineinsight_it.btkeyboard.ui.connection

import android.arch.lifecycle.ViewModelProvider
import com.machineinsight_it.btkeyboard.ui.main.ConnectionViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ConnectionModule {
    @Provides
    fun provideConnectionViewModelFactory(factory: ConnectionViewModelFactory): ViewModelProvider.Factory {
        return factory
    }

    @Provides
    fun provideConnectionViewAccess(fragment: ConnectionFragment): ConnectionViewAccess = fragment
}