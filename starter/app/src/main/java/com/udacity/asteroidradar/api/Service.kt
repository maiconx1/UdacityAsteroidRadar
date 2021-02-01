package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService {
    @GET("neo/rest/v1/feed")
    fun getAsteroidsAsync(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Deferred<String>

    @GET("planetary/apod")
    fun getImageOfTheDayAsync(): Deferred<PictureOfDay>
}

object Network {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { apiKeyInterceptor(it) }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val asteroids: AsteroidService = retrofit.create(AsteroidService::class.java)
}

private fun apiKeyInterceptor(it: Interceptor.Chain): Response {
    val originalRequest = it.request()
    val originalHttpUrl = originalRequest.url()

    val newHttpUrl = originalHttpUrl.newBuilder()
        .addQueryParameter("api_key", BuildConfig.NASA_API_KEY)
        .build()
    val newRequest = originalRequest.newBuilder()
        .url(newHttpUrl)
        .build()

    return it.proceed(newRequest)
}