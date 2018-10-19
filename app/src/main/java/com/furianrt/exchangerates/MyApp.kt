package com.furianrt.exchangerates

import android.app.Application
import com.furianrt.exchangerates.di.application.AppComponent
import com.furianrt.exchangerates.di.application.AppModule
import com.furianrt.exchangerates.di.application.DaggerAppComponent

class MyApp : Application() {

    val component: AppComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
}