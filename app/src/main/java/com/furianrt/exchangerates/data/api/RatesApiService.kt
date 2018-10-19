package com.furianrt.exchangerates.data.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApiService {

    @GET("candles.json")
    fun getExchangeRates(
        @Query("start_time") startDate: String,
        @Query("end_time") endDate: String
    ): Single<RatesApiResponse>
}