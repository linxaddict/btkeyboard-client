package com.machineinsight_it.btkeyboard.ui.base.model

import android.databinding.BaseObservable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : BaseObservable() {
    private val disposables = CompositeDisposable()

    fun registerDisposable(d: Disposable) {
        disposables.add(d)
    }

    fun clearDisposables() {
        disposables.clear()
    }
}