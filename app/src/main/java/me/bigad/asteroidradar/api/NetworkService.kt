/*
 * Copyright (c) 2023.
 *
 *  Developed by : Bigad Aboubakr
 *  Developer website : http://bigad.me
 *  Developer github : https://github.com/Scout4all
 *  Developer Email : bigad@bigad.me
 */

package me.bigad.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import me.bigad.asteroidradar.BuildConfig
import me.bigad.asteroidradar.domain.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface NetworkService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String): String

    @GET("planetary/apod")
    suspend fun getPhotoOfDay(): NetworkPhotoOfDay
}

//add api key over request
val interceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
    val originalHttpUrl = chain.request().url()
    val url = originalHttpUrl.newBuilder()
        .addQueryParameter("api_key", Constants.API_KEY).build();
    request.url(url)
    chain.proceed(request.build())
}


//for http call debug
fun log(): OkHttpClient.Builder {
    val logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
// set your desired log level
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    if (BuildConfig.DEBUG) {
        // development build
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    } else {
        // production build
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
    }
    val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    httpClient.addInterceptor(interceptor)
    httpClient.addInterceptor(logging)
    return httpClient
}

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(log().build())

    .build()

object Network {
    val apiCall = retrofit.create(NetworkService::class.java)
}



