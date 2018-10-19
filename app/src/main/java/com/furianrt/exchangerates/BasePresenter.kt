package com.furianrt.exchangerates

interface BasePresenter<in T : BaseView> {

    fun attachView(view: T)

    fun detachView()
}