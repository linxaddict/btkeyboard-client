package com.machineinsight_it.btkeyboard.ui.main

import android.arch.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides

@Module
class MainModule {
    @Provides
    fun provideMainAccess(mainActivity: MainActivity): MainViewAccess = mainActivity

    @Provides
    fun provideMainViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory = factory
}