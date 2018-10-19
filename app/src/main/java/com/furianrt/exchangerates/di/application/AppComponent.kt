package com.furianrt.exchangerates.di.application

import com.furianrt.exchangerates.di.presenter.PresenterComponent
import com.furianrt.exchangerates.di.presenter.PresenterModule
import dagger.Component

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {

    fun newPresenterComponent(module: PresenterModule): PresenterComponent
}