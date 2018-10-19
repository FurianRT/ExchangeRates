package com.furianrt.exchangerates.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.furianrt.exchangerates.R
import com.furianrt.exchangerates.data.api.Rate
import com.furianrt.exchangerates.utils.networkAvailability
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.jakewharton.rxbinding3.view.clicks
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val CHART_ANIMATION_DURATION = 1000

private fun EditText.addMask(mask: String) {
    val listener = MaskedTextChangedListener(mask, this, null)
    addTextChangedListener(listener)
    onFocusChangeListener = listener
}

private fun MainActivity.makeToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

class MainActivity : AppCompatActivity(), MainContract.View {

    @Inject
    lateinit var mPresenter: MainContract.Presenter

    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var mLoadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_main)

        setupUi()
    }

    private fun setupUi() {
        mLoadingDialog = AlertDialog.Builder(this)
            .setView(R.layout.dialog_loading)
            .setCancelable(false)
            .create()
        mLoadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        chart_rates.apply {
            description = null
            setNoDataText(getString(R.string.info_chart))
            setNoDataTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
            setDrawGridBackground(false)
            setDrawBorders(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
        }

        edit_start_date.addMask("[0000]{-}[00]{-}[00]")
        edit_end_date.addMask("[0000]{-}[00]{-}[00]")

        val disposable = button_load_rates.clicks()
            .throttleFirst(2, TimeUnit.SECONDS)
            .subscribe {
                val startDate = edit_start_date.text.toString()
                val endDate = edit_end_date.text.toString()
                mPresenter.loadRates(startDate, endDate)
            }

        mCompositeDisposable.add(disposable)
    }

    override fun showErrorRange() {
        makeToast(getString(R.string.error_range))
    }

    override fun showErrorInvalidStartDateFormat() {
        makeToast(getString(R.string.errorInvalidDateFormat))
    }

    override fun showErrorInvalidEndDateFormat() {
        makeToast(getString(R.string.errorInvalidDateFormat))
    }

    override fun showErrorBigEndDate() {
        makeToast(getString(R.string.errorDateToBig))
    }

    override fun showErrorBigStartDate() {
        makeToast(getString(R.string.errorDateToBig))
    }

    override fun showErrorNetworkNotAvailable() {
        makeToast(getString(R.string.error_network))
    }

    override fun showErrorLoadingData() {
        makeToast(getString(R.string.error_load_data))
    }

    override fun showMessageEmptyResult() {
        makeToast(getString(R.string.message_empty_result))
    }

    override fun showRates(rates: List<Rate>) {
        val quarters = ArrayList<String>(rates.size)
        val entries = ArrayList<Entry>(rates.size)

        for (i in 0 until rates.size) {
            val rateDate = rates[i].date.split("T")[0]
            quarters.add(rateDate)
            entries.add(Entry(i.toFloat(), rates[i].value))
        }

        val formatter = IAxisValueFormatter { value, _ -> quarters[value.toInt()] }
        val dataSet = LineDataSet(entries, getString(R.string.dollar_rate))
        dataSet.colors = listOf(ContextCompat.getColor(this, R.color.colorAccent))
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        val lineData = LineData(dataSet)

        chart_rates.apply {
            xAxis.valueFormatter = formatter
            xAxis.granularity = 1f
            data = lineData
            animateX(CHART_ANIMATION_DURATION)
        }
    }

    override fun showMinRate(minRate: Float) {
        text_min_rate.text = minRate.toString()
    }

    override fun showMaxRate(maxRate: Float) {
        text_max_rate.text = maxRate.toString()
    }

    override fun showAverageRate(avgRate: Float) {
        text_average_rate.text = avgRate.toString()
    }

    override fun showRateMedian(median: Float) {
        text_rate_median.text = median.toString()
    }

    override fun showRateRange(range: Float) {
        text_rate_range.text = range.toString()
    }

    override fun showLoadingIndicator() {
        mLoadingDialog.show()
    }

    override fun hideLoadingIndicator() {
        mLoadingDialog.dismiss()
    }

    override fun isNetworkAvailable() = networkAvailability(this)

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }
}
