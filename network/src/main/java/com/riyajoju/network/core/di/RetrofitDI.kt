package com.riyajoju.network.core.di

import com.riyajoju.network.core.client.RetrofitClient
import com.riyajoju.network.remote.stocks.AuthApiService
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
            coerceInputValues=true
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
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val timeoutSeconds = 20L
        return OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideCallFactory(client: OkHttpClient): Call.Factory = client

    @Provides
    @Singleton
    @Named("base_url")
    fun baseUrl(): String {
        return "https://db63eda8-a059-4205-a0a3-b81a277abde9.mock.pstmn.io/"
    }

    @Provides
    @Singleton
    @Named("auth_url")
    fun authUrl(): String {
        return "https://db63eda8-a059-4205-a0a3-b81a277abde9.mock.pstmn.io/"
    }

    @Provides
    @Singleton
    @Named("base_retrofit_client")
    fun provideRetrofitClient(
        callFactory: dagger.Lazy<Call.Factory>,
        json: Json,
        @Named("base_url") baseUrl: String
    ): RetrofitClient {
        return RetrofitClient(callFactory, json, baseUrl)
    }


    @Provides
    @Singleton
    @Named("auth_retrofit_client")
    fun provideAuthRetrofitClient(
        callFactory: dagger.Lazy<Call.Factory>,
        json: Json,
        @Named("auth_url") baseUrl: String
    ): RetrofitClient {
        return RetrofitClient(callFactory, json, baseUrl)
    }

    @Provides
    @Singleton
    @Named("base_retrofit")
    fun provideRetrofit(@Named("base_retrofit_client") retrofitClient: RetrofitClient): Retrofit {
        return retrofitClient.retrofit
    }

    @Provides
    @Singleton
    @Named("auth_retrofit")
    fun provideAuthRetrofit(@Named("auth_retrofit_client") retrofitClient: RetrofitClient): Retrofit {
        return retrofitClient.retrofit
    }

    @Provides
    @Singleton
    fun provideStockApiService(@Named("base_retrofit") retrofit: Retrofit): StocksApiService {
        return retrofit.create(StocksApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthApiService(@Named("auth_retrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
