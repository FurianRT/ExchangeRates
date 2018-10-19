package com.furianrt.exchangerates.di.application

import android.app.Application
import android.content.Context
import android.util.Log
import com.furianrt.exchangerates.R
import com.furianrt.exchangerates.data.DataManager
import com.furianrt.exchangerates.data.DataManagerImp
import com.furianrt.exchangerates.data.api.RatesApiService
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule(private val app: Application) {

    @Provides
    @AppScope
    fun provideApplication() = app

    @Provides
    @AppScope
    fun provideContext(): Context = app

    @Provides
    @AppScope
    fun provideDataManager(ratesApi: RatesApiService): DataManager =
        DataManagerImp(ratesApi)

    @Provides
    @AppScope
    fun provideParamInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val url = original.url()
                .newBuilder()
                .addQueryParameter("api_key", app.getString(R.string.api_key))
                .addQueryParameter("data_set", "ecb")
                .addQueryParameter("base", "USD")
                .addQueryParameter("quote", "RUB")
                .addQueryParameter("fields", "averages")
                .build()

            val request = original.newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }
    }

    @Provides
    @AppScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message -> Log.e("myTag", message) }
        return logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(logInterceptor: HttpLoggingInterceptor, paramInterceptor: Interceptor):
            OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
        .addInterceptor(paramInterceptor)
        .build()

    @Provides
    @AppScope
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://web-services.oanda.com/rates/api/v2/rates/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @AppScope
    fun provideWeatherApiService(retrofit: Retrofit): RatesApiService =
        retrofit.create(RatesApiService::class.java)
}