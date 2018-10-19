package com.furianrt.exchangerates.main

import com.furianrt.exchangerates.data.DataManager
import com.furianrt.exchangerates.data.api.Rate
import com.furianrt.exchangerates.utils.schedulers.BaseSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun List<Rate>.getMinRate() = minBy { it.value }!!.value

private fun List<Rate>.getMaxRate() = minBy { it.value }!!.value

private fun List<Rate>.getAvgRate() =
    sumByDouble { it.value.toDouble() }.toFloat() / size

private fun List<Rate>.getMedian(): Float {
    val sorted = sortedBy { it.value }
    return when {
        isEmpty() -> 0f
        size == 1 -> sorted[0].value
        //если список нечетный, то медианой будет центральный элемент
        size % 2 != 0 -> sorted[size / 2].value
        //если список четный, то медианой будет среднее арифметическое между двумя центральными элементами
        else -> (sorted[size / 2].value + sorted[(size / 2 - 1)].value) / 2
    }
}

private fun List<Rate>.getRange(): Float {
    val sorted = sortedBy { it.value }
    return when {
        size == 1 || isEmpty() -> 0f
        size % 2 != 0 ->
            //если список нечетный, то находим медианы двух половин,
            //неучитывая центральный элемент, и вычитаем их
            sorted.subList(size / 2 + 1, size).getMedian() - sorted.subList(0, size / 2).getMedian()
        else ->
            //если список четный, то делим список на две половины,
            //находим их медианы и вычитаем их
            sorted.subList(size / 2, size).getMedian() - sorted.subList(0, size / 2).getMedian()
    }
}

class MainPresenter(
    private val mDataManager: DataManager,
    private val mSchedulerProvider: BaseSchedulerProvider
) : MainContract.Presenter {

    private var mView: MainContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: MainContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun loadRates(start: String, end: String) {
        mView?.let { view ->
            if (!view.isNetworkAvailable()) {
                view.showErrorNetworkNotAvailable()
                return
            }
            val startDate = parseDateFromString(start, "yyyy-MM-dd")
            val endDate = parseDateFromString(end, "yyyy-MM-dd")
            val currentDate = Date()
            when {
                startDate == null -> view.showErrorInvalidStartDateFormat()
                endDate == null -> view.showErrorInvalidEndDateFormat()
                startDate > currentDate -> view.showErrorBigStartDate()
                endDate > currentDate -> view.showErrorBigEndDate()
                startDate > endDate -> view.showErrorRange()
                else -> getRates(startDate, endDate)
            }
        }
    }

    private fun getRates(startDate: Date, endDate: Date) {
        mView?.showLoadingIndicator()
        val disposable = mDataManager.getExchangeRates(startDate, endDate)
            .subscribeOn(mSchedulerProvider.io())
            .observeOn(mSchedulerProvider.ui())
            .subscribe(
                { rates ->
                    if (rates.isEmpty()) {
                        mView?.showMessageEmptyResult()
                    } else {
                        mView?.showRates(rates)
                        mView?.showMinRate(rates.getMinRate())
                        mView?.showMaxRate(rates.getMaxRate())
                        mView?.showAverageRate(rates.getAvgRate())
                        mView?.showRateMedian(rates.getMedian())
                        mView?.showRateRange(rates.getRange())
                    }
                    mView?.hideLoadingIndicator()
                }, {
                    mView?.hideLoadingIndicator()
                    mView?.showErrorLoadingData()
                }
            )

        mCompositeDisposable.add(disposable)
    }

    private fun parseDateFromString(date: String, pattern: String): Date? =
        try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.parse(date)
        } catch (e: ParseException) {
            null
        }
}