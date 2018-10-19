package com.furianrt.exchangerates.di.presenter

import android.content.Context
import com.furianrt.exchangerates.data.DataManager
import com.furianrt.exchangerates.main.MainContract
import com.furianrt.exchangerates.main.MainPresenter
import com.furianrt.exchangerates.utils.schedulers.BaseSchedulerProvider
import com.furianrt.exchangerates.utils.schedulers.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class PresenterModule(private val context: Context) {

    @Provides
    @PresenterScope
    fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider()

    @Provides
    @PresenterScope
    fun provideMainPresenter(dataManager: DataManager, scheduler: BaseSchedulerProvider)
            : MainContract.Presenter = MainPresenter(dataManager, scheduler)
}