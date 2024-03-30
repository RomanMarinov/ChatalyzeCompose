package com.dev_marinov.chatalyze.di

import android.util.Log
import com.dev_marinov.chatalyze.BuildConfig
import com.dev_marinov.chatalyze.data.auth.dto.UserTokensDetails
import com.dev_marinov.chatalyze.domain.model.auth.PairOfTokens
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.DecodeToken
import com.dev_marinov.chatalyze.presentation.util.ShowToastHelper
import com.dev_marinov.chatalyze.presentation.util.TokenPayload
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.client.utils.buildHeaders
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_OK
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import org.json.JSONObject
import javax.inject.Provider
import kotlin.coroutines.CoroutineContext

class MyTestInterceptor(
    private val authRepositoryProvider: Provider<AuthRepository>,
    private val preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>
) : HttpClientPlugin<ResponseObserver.Config, MyTestInterceptor> {

    override val key: AttributeKey<MyTestInterceptor> = AttributeKey("MyTestInterceptor")

    override fun install(plugin: MyTestInterceptor, scope: HttpClient) {
        scope.requestPipeline.intercept(HttpRequestPipeline.Render) { response ->

            var res = context.url.build()
            Log.d("4444", " MyTestInterceptor res=" + res)
//
            if (context.url.toString().contains(Constants.PART_URL_MESSAGES)) {
                val refreshToken = authRepositoryProvider.get().getRefreshTokensFromDataStore.first()
                val accessToken = authRepositoryProvider.get().getAccessTokensFromDataStore.first()
                if (accessToken.isNotEmpty()) {
                   // val accessToken = "wefwefwefwef"
                    context.headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
                }


               // val newResponse = chainContinueRequest4(accessToken)
//
//                proceedWith(newResponse)
            } else {
//                proceedWith(response)
                //proceed()
            }

            runBlocking {
                Log.d("4444", " проверушка 3")
                val currentUrl = context.url
                Log.d("4444", " MyInterceptor currentUrl=" + currentUrl)
                Log.d("4444", " MyInterceptor Thread.currentThread()=" + Thread.currentThread())

                val refreshToken: String = authRepositoryProvider.get().getRefreshTokensFromDataStore.first()
                val accessToken: String = authRepositoryProvider.get().getAccessTokensFromDataStore.first()
                val refreshTokenPayload: TokenPayload = DecodeToken.execute(token = refreshToken)

                val userTokensDetails = UserTokensDetails(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = refreshTokenPayload.userId
                )
                val httpResponse: HttpResponse = updateTokensRequest(userTokensDetails = userTokensDetails)
                Log.d("4444", " проверушка 4")
                httpResponse.body<String>().let {
                    when (httpResponse.status.value) {
                        HTTP_OK -> {
                            Log.d("4444", " MyInterceptor первый уровень HTTP_OK")
                            if (it.contains("\"httpStatusCode\"") && it.contains("\"message\"")) {
                                val jsonResponse = JSONObject(it)
                                val httpStatusCode = jsonResponse.optString("httpStatusCode")
                                val message = jsonResponse.optString("message")
                                Log.d("4444", " проверушка 5")
                                when (httpStatusCode.toInt()) {
                                    HTTP_OK -> {
                                        Log.d("4444", " MyInterceptor второй уровень HTTP_OK")
                                        val res = JsonParser.parseString(message)
                                        val pairOfTokensNew = Gson().fromJson(res, PairOfTokens::class.java)

                                        authRepositoryProvider.get().savePairTokens(
                                            pairOfTokens = PairOfTokens(
                                                accessToken = pairOfTokensNew.accessToken,
                                                refreshToken = pairOfTokensNew.refreshToken
                                            )
                                        )
                                        Log.d("4444", " MyInterceptor pairOfTokensNew=" + pairOfTokensNew)

                                        try {
                                            // не действительный токен
//                                          //val resToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4MCIsImV4cCI6MTcxMDg3NTM1OSwidXNlcklkIjoxOH0.LzyxWt8Wq8WpfH7qctO6yNtsCSeobBV62mLNTvEA1-W"

//                                            Log.d("4444", " MyInterceptor accessToken=" + accessToken)
//                                            Log.d("4444", " MyInterceptor scope=" + scope)

                                            if (accessToken.isNotEmpty()) {
                                                // val accessToken = "wefwefwefwef"
                                                context.headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
                                            }

//                                            val responseChain: HttpResponse = chainContinueRequest4(
//                                                accessToken = accessToken,
//                                                httpClient = scope
//                                            )
//                                            Log.d("4444", " MyInterceptor responseChain=" + responseChain)
//                                            if (responseChain.status.value == 401) {
//                                                preferencesDataStoreRepositoryProvider.get()
//                                                    .saveStateUnauthorized(isUnauthorized = true)
//                                            }
//                                            Log.d("4444", " проверушка 6")
                                            //httpResponseMain = responseChain
                                        } catch (e: Exception) {
                                            Log.d("4444", " try catch MyInterceptor chainContinueRequest3 e=" + e.localizedMessage)
                                        }
                                    }

                                    HTTP_NOT_FOUND -> {
                                        Log.d("4444", " MyInterceptor второй уровень HTTP_NOT_FOUND")
//                                        httpResponseMain = badResponse(
//                                            httpClient = scope,
//                                            httpCode = HTTP_NOT_FOUND,
//                                            message = message
//                                        )
                                        Log.d("4444", " проверушка 7")
                                        preferencesDataStoreRepositoryProvider.get()
                                            .saveStateNotFoundRefreshToken(isNotFound = true)
                                    }

                                    HTTP_UNAUTHORIZED -> {
                                        Log.d("4444", " MyInterceptor второй уровень HTTP_UNAUTHORIZED")
//                                        httpResponseMain = badResponse(
//                                            httpClient = scope,
//                                            httpCode = HTTP_UNAUTHORIZED,
//                                            message = message
//                                        )
                                        Log.d("4444", " проверушка 8")
                                        preferencesDataStoreRepositoryProvider.get()
                                            .saveStateUnauthorized(isUnauthorized = true)
                                    }

                                    else -> {
                                        Log.d("4444", " проверушка 9")
                                        Log.d("4444", " MyInterceptor второй уровень ELSE")
                                        preferencesDataStoreRepositoryProvider.get()
                                            .saveFailureUpdatePairToken(isFailure = true)
                                    }
                                }
                            } else {
                                Log.d("4444", " проверушка 10")
                                Log.d("4444", " MyInterceptor первый уровень contains NULL")
                            }
                        }

                        else -> {
                            Log.d("4444", " проверушка 11")
                            preferencesDataStoreRepositoryProvider.get()
                                .saveInternalServerError(isError = true)
                        }
                    }
                }
            }


        }

        scope.responsePipeline.intercept(HttpResponsePipeline.After) { response ->
            if (response.response == HttpStatusCode.Unauthorized.value) {
                // Обработка ответа с кодом статуса UNAUTHORIZED (401) здесь
                // Это место для выполнения действий при получении ошибки авторизации
                Log.d("4444", " Unauthorized response received")
                preferencesDataStoreRepositoryProvider.get()
                    .saveStateUnauthorized(isUnauthorized = true)
            } else {
                Log.d("4444", " Unauthorized received=" + response.response)
            }
        }
    }

    override fun prepare(block: ResponseObserver.Config.() -> Unit): MyTestInterceptor {
        return this
    }
    private suspend fun chainContinueRequest4(
        accessToken: String,
      //  httpClient: HttpClient,
    ): HttpResponse {
        val httpClient = HttpClient(CIO)
        return try {
            Log.d("4444", " chainContinueRequest4 accessToken=" + accessToken)
            val httpRequest = HttpRequestBuilder()
            httpRequest.headers {
                append("Authorization", "Bearer $accessToken")
            }
            httpClient.request(httpRequest)
        } catch (e: Exception) {
            Log.d("4444", " try catch chainContinueRequest4 e=" + e)
            badResponse(httpClient, 0, e.toString())
        }
    }

    private suspend fun updateTokensRequest(userTokensDetails: UserTokensDetails): HttpResponse {
        val gson = Gson()
        val json = gson.toJson(userTokensDetails)
        val body = TextContent(json, ContentType.Application.Json)
        val url = BuildConfig.API_URL_HTTP + Constants.PART_URL_UPDATE_TOKENS

        val client = HttpClient(CIO)

        val httpResponse: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(body = body)
        }
        return httpResponse
    }

    private fun badResponse(httpClient: HttpClient, httpCode: Int, message: String): HttpResponse {
        return object : HttpResponse() {
            override val call: HttpClientCall = HttpClientCall(httpClient)
            @InternalAPI
            override val content: ByteReadChannel get() = ByteReadChannel(ByteArray(0))
            override val coroutineContext: CoroutineContext get() = Job()
            override val headers: Headers get() = Headers.Empty
            override val requestTime: GMTDate get() = GMTDate(0)
            override val responseTime: GMTDate = GMTDate(0)
            override val status: HttpStatusCode get() = HttpStatusCode(httpCode, message)
            override val version: HttpProtocolVersion get() = HttpProtocolVersion.HTTP_1_1
        }
    }
}