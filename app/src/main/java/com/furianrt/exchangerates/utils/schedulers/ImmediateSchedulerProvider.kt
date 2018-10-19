package com.furianrt.exchangerates.utils.schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

//Для тестирования
class ImmediateSchedulerProvider : BaseSchedulerProvider {

    override fun computation(): Scheduler = Schedulers.trampoline()

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()
}