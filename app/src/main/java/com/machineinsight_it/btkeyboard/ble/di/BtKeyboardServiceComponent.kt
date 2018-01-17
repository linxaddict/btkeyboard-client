package com.machineinsight_it.btkeyboard.ble.di

import com.machineinsight_it.btkeyboard.ble.BtKeyboardService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [BtKeyboardServiceModule::class, BluetoothModule::class])
interface BtKeyboardServiceComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun service(service: BtKeyboardService): Builder
        fun build(): BtKeyboardServiceComponent
    }

    fun inject(service: BtKeyboardService)
}