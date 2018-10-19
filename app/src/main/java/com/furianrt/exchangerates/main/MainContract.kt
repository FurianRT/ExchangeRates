package com.furianrt.exchangerates.main

import com.furianrt.exchangerates.BasePresenter
import com.furianrt.exchangerates.BaseView
import com.furianrt.exchangerates.data.api.Rate

interface MainContract {

    interface View : BaseView {

        fun showErrorRange()

        fun showRates(rates: List<Rate>)

        fun showErrorInvalidStartDateFormat()

        fun showErrorInvalidEndDateFormat()

        fun showErrorBigStartDate()

        fun showErrorBigEndDate()

        fun showMessageEmptyResult()

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showMinRate(minRate: Float)

        fun showMaxRate(maxRate: Float)

        fun showAverageRate(avgRate: Float)

        fun showRateMedian(median: Float)

        fun showRateRange(range: Float)

        fun showErrorNetworkNotAvailable()

        fun isNetworkAvailable(): Boolean

        fun showErrorLoadingData()
    }

    interface Presenter : BasePresenter<View> {

        fun loadRates(start: String, end: String)
    }
}