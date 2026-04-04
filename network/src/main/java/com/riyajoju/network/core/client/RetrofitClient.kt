package com.riyajoju.network.core.client

import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Singleton
class RetrofitClient(
    private val lazyOkHttpClient: dagger.Lazy<Call.Factory>,
    private val json: Json,
    private val baseUrl: String
) {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .callFactory { lazyOkHttpClient.get().newCall(it) }
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
}