package com.dev_marinov.chatalyze.di


import com.dev_marinov.chatalyze.BuildConfig
import com.dev_marinov.chatalyze.data.auth.AuthApiService
import com.dev_marinov.chatalyze.data.call.remote.CallApiService
import com.dev_marinov.chatalyze.data.chat.ChatApiService
import com.dev_marinov.chatalyze.data.chats.ChatsApiService
import com.dev_marinov.chatalyze.data.firebase.register.FirebaseApiService
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApiService(retrofit: Retrofit): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatsApiService(retrofit: Retrofit): ChatsApiService {
        return retrofit.create(ChatsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCallApiService(retrofit: Retrofit): CallApiService {
        return retrofit.create(CallApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): FirebaseApiService {
        return retrofit.create(FirebaseApiService::class.java)
    }


    //http://0.0.0.0:8080/register
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL_HTTP)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

//    @Provides
//    @Singleton
//    fun provideHttpClient(): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor { chain: Interceptor.Chain ->
//                chain.proceed(chain.request().newBuilder().also {
//                    it.addHeader("Authorization", "Bearer $authToken")
//                }.build())
//            }
//            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//            .connectTimeout(100, TimeUnit.SECONDS)
//            .readTimeout(100, TimeUnit.SECONDS)
//            .build()
//    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyRateGsonConverter(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }
}