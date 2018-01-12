package com.machineinsight_it.btkeyboard.ui.main

import dagger.Module
import dagger.Provides

@Module
class MainModule {
    @Provides
    fun provideMainAccess(mainActivity: MainActivity): MainViewAccess = mainActivity
}