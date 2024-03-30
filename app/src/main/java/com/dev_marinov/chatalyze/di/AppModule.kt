package com.dev_marinov.chatalyze.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.dev_marinov.chatalyze.data.firebase.notification.PushNotificationManagerImpl
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.PushNotificationManager
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.util.ConnectivityObserver
import com.dev_marinov.chatalyze.presentation.util.NetworkObserverHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Provider
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideConnectivityObserver(application: Application): ConnectivityObserver {
        return NetworkObserverHelper(application)
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        @ApplicationContext context: Context,
        authRepositoryProvider: Provider<AuthRepository>,
        preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>,
    ): HttpClient {
        val httpClient = HttpClient(CIO) {
            install(KtorNetworkInterceptor(context = context,
                preferencesDataStoreRepositoryProvider = preferencesDataStoreRepositoryProvider))
            install(KtorUpdateTokenInterceptor(authRepositoryProvider = authRepositoryProvider,
                preferencesDataStoreRepositoryProvider = preferencesDataStoreRepositoryProvider))
            install(KtorAuthInterceptor(authRepositoryProvider = authRepositoryProvider,
                preferencesDataStoreRepositoryProvider = preferencesDataStoreRepositoryProvider))
            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("4444", " ResponseObserver HTTP onResponse=" + response.status.value)
                }
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("HTTP Client", message)
                    }
                }
                level = LogLevel.ALL
            }
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 100000
                requestTimeoutMillis = 100000
                connectTimeoutMillis = 100000
            }
        }
        return httpClient
    }


//    @Provides
//    @Singleton
//    fun provideHttpClient(
//        @ApplicationContext context: Context,
//        authRepositoryProvider: Provider<AuthRepository>,
//        preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>,
//    ): HttpClient {
//        val httpClient = HttpClient(CIO) {
//
//            install(MyInterceptor(ResponseObserver))
//
////            install(ResponseObserver) {
////                onResponse {
////                    val res = it.request.url.toString()
////                    Log.d("4444", " provideHttpClient res=" + res)
////                }
////            }
//
//            install(Logging)
//            install(WebSockets)
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//
//
//
//
//        return runBlocking {
//
//
//
////            val res = httpClient.get { url }
////            Log.d("4444", " url.toString()=" + res.request.url.toString())
//
//            // Трансформация по запросу
//            httpClient.requestPipeline.intercept(HttpRequestPipeline.Transform) { payload ->
//
//            }
//
//            // Преобразования по ответу
//            httpClient.responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
//                // Делаем что-нибудь с ответом, а затем отправляем его на следующий уровень
//                //val result = transformMyResponse(info, body)
//                Log.d("4444", " Трансформация по запросу info=" + info + " body=" + body)
//                //proceedWith(result)
//            }
//
//           // httpClient.close()
//
//            httpClient
//        }
//    }




//    suspend fun updateTokensRequest(userTokensDetails: UserTokensDetails): HttpResponse {
//        val client = HttpClient() {
//            install(JsonFeature) {
//                serializer = GsonSerializer()
//            }
//            HttpResponsePipeline.Transform.safe {
//                // Выполните здесь ваше промежуточное преобразование ответа
//                val transformedResponse = transformMyResponse(it, context.response.content)
//                HttpResponseContainer(HttpStatusCode.OK, Headers.Empty, transformedResponse)
//            }
//        }
//
//        val response = client.post<HttpResponse> {
//            url(BuildConfig.API_URL_HTTP + Constants.PART_URL_UPDATE_TOKENS)
//            contentType(ContentType.Application.Json)
//            body = userTokensDetails
//        }
//
//        client.close()
//        return response
//    }


//    @Provides
//    @Singleton
//    fun provideHttpClient(
////        @ApplicationContext context: Context,
////        authRepositoryProvider: Provider<AuthRepository>,
////        preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>,
//    ): HttpClient {
//        val httpClient = HttpClient(CIO) {
//
//
//
//
//
//            install(res) {
//
//
//
//                intercept(HttpRequestPipeline.Phases.Send) {
//                    // Логика ваших перехватчиков
//                    // Например, вы можете добавить несколько перехватчиков здесь
//                    // Передача дальше цепочки
//                    proceed()
//                }
//                // Добавьте другие перехватчики, если необходимо
//            }
//
//
//
//
//                engine {
//
//                }
//                intercept() {
//
//                }
//
////            val res = HttpRequestPipeline
////
////            install()
////
////            install(HttpRequestPipeline.Phases) {
////                // Перехватываем запрос перед отправкой
////                sendPipeline.intercept(HttpRequestPipeline.Before) {
////                    // Ваш код для обработки запроса
////                    println("Перехвачен запрос: ${context.url}")
////                }
////            }
////
////            install(HttpResponsePipeline) {
////                // Перехватываем ответ после получения
////                receivePipeline.intercept(HttpResponsePipeline.After) {
////                    // Ваш код для обработки ответа
////                    println("Получен ответ: ${subject.status}")
////                }
////            }
//            install(Logging)
//            install(WebSockets)
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//
//
//        // Трансформация по запросу
//        httpClient.requestPipeline.intercept(HttpRequestPipeline.Transform) { playload ->
//            // Делаем что-то с запросом, затем отправляем его на следующий уровень
//            val result = transformMyRequest(payload)
//            proceedWith(result)
//        }
//
//        // Преобразования по ответу
//        httpClient.responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
//            // Делаем что-нибудь с ответом, а затем отправляем его на следующий уровень
//            val result = transformMyResponse(info, body)
//            proceedWith(result)
//        }
//
//
//
//        return httpClient
//    }

//    @Provides
//    @Singleton
//    fun provideHttpClient(): HttpClient {
//        return HttpClient(CIO) {
//            install(Logging)
//            install(WebSockets)
//            install(ContentNegotiation) {
//                json()
//            }
//
//
//
//        }
//    }


////////////////////////////////
//    @Provides
//    @Singleton
//    fun provideHttpClient(): HttpClient {
//        return HttpClient(CIO) {
//            install(Logging)
//            install(WebSockets)
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//    }


    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        preferencesDataStoreRepository: PreferencesDataStoreRepository,
        roomRepository: RoomRepository,
    ): PushNotificationManager {
        return PushNotificationManagerImpl(context, preferencesDataStoreRepository, roomRepository)
    }
}