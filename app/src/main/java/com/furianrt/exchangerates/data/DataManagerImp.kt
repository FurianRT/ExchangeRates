package com.furianrt.exchangerates.data

import com.furianrt.exchangerates.data.api.Rate
import com.furianrt.exchangerates.data.api.RatesApiService
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*

class DataManagerImp(
    private val mRatesApi: RatesApiService
) : DataManager {

    override fun getExchangeRates(startDate: Date, endDate: Date): Single<List<Rate>> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return mRatesApi.getExchangeRates(sdf.format(startDate), sdf.format(endDate))
            .map { response -> response.rates }
    }
}