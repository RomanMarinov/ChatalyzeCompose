package com.dev_marinov.chatalyze.di

import android.util.Log
import com.dev_marinov.chatalyze.BuildConfig
import com.dev_marinov.chatalyze.data.auth.dto.UserTokensDetails
import com.dev_marinov.chatalyze.domain.model.auth.PairOfTokens
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.DecodeToken
import com.dev_marinov.chatalyze.presentation.util.TokenPayload
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_OK
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import org.json.JSONObject
import javax.inject.Provider

class KtorUpdateTokenInterceptor(
    private val authRepositoryProvider: Provider<AuthRepository>,
    private val preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>,
) : HttpClientPlugin<ResponseObserver.Config, KtorUpdateTokenInterceptor> {

    override val key: AttributeKey<KtorUpdateTokenInterceptor> = AttributeKey("NewUpdateTokenInterceptor")

    override fun install(plugin: KtorUpdateTokenInterceptor, scope: HttpClient) {
        scope.requestPipeline.intercept(HttpRequestPipeline.Render) { response ->

            var res = context.url.build()
            Log.d("4444", " qwe NewUpdateTokenInterceptor res=" + res)
            if (context.url.toString().contains(Constants.PART_URL_MESSAGES)) {

                runBlocking {
                    Log.d("4444", " проверушка 3")
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
                                Log.d("4444", " qwe NewUpdateTokenInterceptor первый уровень HTTP_OK")
                                if (it.contains("\"httpStatusCode\"") && it.contains("\"message\"")) {
                                    val jsonResponse = JSONObject(it)
                                    val httpStatusCode = jsonResponse.optString("httpStatusCode")
                                    val message = jsonResponse.optString("message")
                                    Log.d("4444", " проверушка 5")
                                    when (httpStatusCode.toInt()) {
                                        HTTP_OK -> {
                                            Log.d("4444", " qwe NewUpdateTokenInterceptor второй уровень HTTP_OK")
                                            val jsonParser = JsonParser.parseString(message)
                                            val pairOfTokensNew = Gson().fromJson(jsonParser, PairOfTokens::class.java)

                                            authRepositoryProvider.get().savePairTokens(
                                                pairOfTokens = PairOfTokens(
                                                    accessToken = pairOfTokensNew.accessToken,
                                                    refreshToken = pairOfTokensNew.refreshToken
                                                )
                                            )
                                            proceed()
                                        }

                                        HTTP_NOT_FOUND -> {
                                            Log.d("4444", " qwe NewUpdateTokenInterceptor второй уровень HTTP_NOT_FOUND")
                                            Log.d("4444", " проверушка 7")
                                            preferencesDataStoreRepositoryProvider.get()
                                                .saveStateNotFoundRefreshToken(isNotFound = true)
                                            finish()
                                        }

                                        HTTP_UNAUTHORIZED -> {
                                            Log.d("4444", " qwe NewUpdateTokenInterceptor второй уровень HTTP_UNAUTHORIZED")
                                            Log.d("4444", " проверушка 8")
                                            preferencesDataStoreRepositoryProvider.get()
                                                .saveStateUnauthorized(isUnauthorized = true)
                                            finish()
                                        }

                                        else -> {
                                            Log.d("4444", " проверушка 9")
                                            Log.d("4444", " qwe NewUpdateTokenInterceptor второй уровень ELSE")
                                            preferencesDataStoreRepositoryProvider.get()
                                                .saveFailureUpdatePairToken(isFailure = true)
                                            finish()
                                        }
                                    }
                                } else {
                                    Log.d("4444", " проверушка 10")
                                    Log.d("4444", " qwe NewUpdateTokenInterceptor первый уровень contains NULL")
                                }
                            }

                            else -> {
                                Log.d("4444", " проверушка 11")
                                preferencesDataStoreRepositoryProvider.get().saveInternalServerError(isError = true)
                                finish()
                            }
                        }
                    }
                }
            }
        }

        scope.responsePipeline.intercept(HttpResponsePipeline.After) { response ->
            if (context.response.status.value == HttpStatusCode.Unauthorized.value) {
                Log.d("4444", " qwe NewUpdateTokenInterceptor Unauthorized response=" + context.request.url + " code=" + context.response.status.value)
                preferencesDataStoreRepositoryProvider.get().saveStateUnauthorized(isUnauthorized = true)
            } else {
                Log.d("4444", " qwe NewUpdateTokenInterceptor some 1 response=" + context.request.url + " code=" + context.response.status.value)
            }
            Log.d("4444", " qwe NewUpdateTokenInterceptor some 2 response=" + context.request.url + " code=" + context.response.status.value)
            proceed()
        }
    }

    override fun prepare(block: ResponseObserver.Config.() -> Unit): KtorUpdateTokenInterceptor {
        return this
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
}