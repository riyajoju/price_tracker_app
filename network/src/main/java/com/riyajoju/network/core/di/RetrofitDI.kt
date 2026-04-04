package com.riyajoju.network.core.di

import com.riyajoju.network.core.client.RetrofitClient
import com.riyajoju.network.remote.stocks.StocksApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitDI {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): Call.Factory {
        val connectTimeoutSeconds = 15L
        val readTimeoutSeconds = 20L
        val writeTimeoutSeconds = 20L
        return OkHttpClient.Builder().connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor).build()
    }

    @Provides
    @Singleton
    @Named("base_url")
    fun baseUrl(): String {
        return "https://db63eda8-a059-4205-a0a3-b81a277abde9.mock.pstmn.io/"
    }


    @Provides
    @Singleton
    fun provideRetrofitClient(
        callFactory: dagger.Lazy<Call.Factory>,
        json: Json,
        @Named("base_url") baseUrl: String
    ): RetrofitClient {
        return RetrofitClient(callFactory, json, baseUrl)
    }

    @Provides
    @Singleton
    fun provideRetrofit(retrofitClient: RetrofitClient): Retrofit {
        return retrofitClient.retrofit
    }

    @Provides
    @Singleton
    fun provideStockApiService(retrofit: Retrofit): StocksApiService {
        return retrofit.create(StocksApiService::class.java)
    }
}

