package com.furianrt.exchangerates.data

import com.furianrt.exchangerates.data.api.Rate
import io.reactivex.Single
import java.util.*

interface DataManager {

    fun getExchangeRates(startDate: Date, endDate: Date): Single<List<Rate>>
}