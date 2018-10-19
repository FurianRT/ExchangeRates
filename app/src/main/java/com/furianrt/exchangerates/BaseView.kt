package com.furianrt.exchangerates

import android.content.Context
import com.furianrt.exchangerates.di.presenter.PresenterComponent
import com.furianrt.exchangerates.di.presenter.PresenterModule

interface BaseView {

    fun getPresenterComponent(context: Context): PresenterComponent {
        return (context.applicationContext as MyApp)
                .component
                .newPresenterComponent(PresenterModule(context))
    }
}