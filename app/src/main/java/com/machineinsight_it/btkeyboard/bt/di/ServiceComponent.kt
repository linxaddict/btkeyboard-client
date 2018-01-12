package com.machineinsight_it.btkeyboard.bt.di

import com.machineinsight_it.btkeyboard.bt.BtKeyboardService
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceModule::class, BluetoothModule::class])
interface ServiceComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun service(service: BtKeyboardService): Builder
        fun build(): ServiceComponent
    }

    fun inject(service: BtKeyboardService)
}