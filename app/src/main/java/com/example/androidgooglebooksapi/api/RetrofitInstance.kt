package com.example.androidgooglebooksapi.api

import com.example.androidgooglebooksapi.util.Constans.Companion.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val getClient by lazy {

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Connection", "close")
                .method(original.method(), original.body())
                .build()
            chain.proceed(request)
        }

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getApiRepository: ApiRepository by lazy {
        getClient.create(ApiRepository::class.java)
    }


    fun getResponseStatusCode(response: Response<*>?): Int {
        return response?.code() ?: 404
    }








}