package com.devis.get_github_user.api

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection

/**
 * Created by devis on 22/06/20
 */

class ApiClient(url: String) {

    private var mApiServices: ApiServices

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient

        val hostnameVerifier = HostnameVerifier { hostname, session ->
            val hv = HttpsURLConnection.getDefaultHostnameVerifier()
            hv.verify(hostname, session)
        }

        val gson = GsonBuilder()
            .setLenient()
            .create()

        client = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .hostnameVerifier(hostnameVerifier)
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .baseUrl(url)
            .build()

        mApiServices = retrofit.create(ApiServices::class.java)
    }

    fun getApiServices(): ApiServices {
        return mApiServices
    }

}