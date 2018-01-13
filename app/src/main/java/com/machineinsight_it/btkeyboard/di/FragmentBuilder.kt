package com.machineinsight_it.btkeyboard.di

import com.machineinsight_it.btkeyboard.ui.connection.ConnectionFragment
import com.machineinsight_it.btkeyboard.ui.connection.ConnectionModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {
    @ContributesAndroidInjector(modules = [ConnectionModule::class])
    abstract fun bindConnectionFragment(): ConnectionFragment
}