package com.furianrt.exchangerates.di.presenter

import com.furianrt.exchangerates.main.MainActivity
import dagger.Subcomponent

@PresenterScope
@Subcomponent(modules = [PresenterModule::class])
interface PresenterComponent {

    fun inject(activity: MainActivity)
}