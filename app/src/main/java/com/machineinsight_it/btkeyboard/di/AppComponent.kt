package com.machineinsight_it.btkeyboard.di

import android.app.Application
import com.machineinsight_it.btkeyboard.BtKeyboardApplication
import com.machineinsight_it.btkeyboard.ble.di.BluetoothModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuilder::class,
    FragmentBuilder::class, BluetoothModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

    fun inject(application: BtKeyboardApplication)
}