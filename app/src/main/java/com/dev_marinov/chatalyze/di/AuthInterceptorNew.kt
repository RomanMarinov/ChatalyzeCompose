package com.dev_marinov.chatalyze.di

import android.content.Context
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_OK
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import org.json.JSONObject
import javax.inject.Provider

class AuthInterceptorNew(
    private val context: Context,
    private val authRepositoryProvider: Provider<AuthRepository>,
    private val preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("4444", " NetworkModule AuthInterceptor intercept выполнился")
        var responseMain: Response? = null

        val currentUrl = chain.request().url.toString()
        Log.d("4444", " NetworkModule AuthInterceptor currentUrl=" + currentUrl)
        if (
        //currentUrl.contains(Constants.PART_URL_LOGOUT, true) ||
        //currentUrl.contains(Constants.PART_CHAT_COMPANION, true) ||
            currentUrl.contains(Constants.PART_URL_CHATS, true)
//            currentUrl.contains(Constants.PART_URL_DELETE_PROFILE, true)
        ) {
            runBlocking {
                val refreshToken: String = authRepositoryProvider.get().getRefreshTokensFromDataStore.first()
                val accessToken: String = authRepositoryProvider.get().getAccessTokensFromDataStore.first()
                val refreshTokenPayload: TokenPayload = DecodeToken.execute(token = refreshToken)

                val userTokensDetails = UserTokensDetails(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = refreshTokenPayload.userId
                )

                Log.d("4444", " NetworkModule AuthInterceptor userTokensDetails=" + userTokensDetails)
                val response: Response = updateTokensRequest(userTokensDetails = userTokensDetails)

                response.body.string().let {
                    when (response.code) {
                        HTTP_OK -> {
                            Log.d("4444", " NetworkModule AuthInterceptor первый уровень HTTP_OK")
                            if (it.contains("\"httpStatusCode\"") && it.contains("\"message\"")) {
                                val jsonResponse = JSONObject(it)
                                val httpStatusCode = jsonResponse.optString("httpStatusCode")
                                val message = jsonResponse.optString("message")

                                when (httpStatusCode.toInt()) {
                                    HTTP_OK -> {
                                        Log.d("4444", " NetworkModule AuthInterceptor второй уровень HTTP_OK")
                                        val res = JsonParser.parseString(message)

                                        val pairOfTokensNew = Gson().fromJson(res, PairOfTokens::class.java)
                                        Log.d("4444", " NetworkModule AuthInterceptor pairOfTokensNew=" + pairOfTokensNew)
                                        authRepositoryProvider.get().savePairTokens(
                                            pairOfTokens = PairOfTokens(
                                                accessToken = pairOfTokensNew.accessToken,
                                                refreshToken = pairOfTokensNew.refreshToken
                                            )
                                        )
                                       // Log.d("4444", " NetworkModule AuthInterceptor pairOfTokensNew.accessToken=" + pairOfTokensNew.accessToken)
                                        // не действительный токен
                                        //val resToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4MCIsImV4cCI6MTcxMDg3NTM1OSwidXNlcklkIjoxOH0.LzyxWt8Wq8WpfH7qctO6yNtsCSeobBV62mLNTvEA1-W"
                                        val responseChain: Response = chainContinueRequest(
                                            accessToken = accessToken,
                                            chain = chain
                                        )
                                        if (responseChain.code == 401) {
                                            preferencesDataStoreRepositoryProvider.get()
                                                .saveStateUnauthorized(isUnauthorized = true)
                                        }
                                        responseMain = responseChain
                                    }

                                    HTTP_NOT_FOUND -> {
                                        Log.d("4444", " NetworkModule AuthInterceptor второй уровень HTTP_NOT_FOUND")
                                        responseMain = badResponse(
                                            chain = chain,
                                            httpCode = HTTP_NOT_FOUND,
                                            message = message
                                        )
                                        preferencesDataStoreRepositoryProvider.get()
                                            .saveStateNotFoundRefreshToken(isNotFound = true)
                                    }

                                    HTTP_UNAUTHORIZED -> {
                                        Log.d("4444", " NetworkModule AuthInterceptor второй уровень HTTP_UNAUTHORIZED")
                                        responseMain = badResponse(
                                            chain = chain,
                                            httpCode = HTTP_UNAUTHORIZED,
                                            message = message
                                        )
                                        preferencesDataStoreRepositoryProvider.get()
                                            .saveStateUnauthorized(isUnauthorized = true)
                                    }

                                    else -> {
                                    Log.d("4444", " NetworkModule AuthInterceptor второй уровень ELSE")
                                        preferencesDataStoreRepositoryProvider.get()
                                            .saveFailureUpdatePairToken(isFailure = true)
                                    }
                                }
                            } else {
                                Log.d("4444", " NetworkModule AuthInterceptor первый уровень contains NULL")
                            }
                        } else -> {
                        preferencesDataStoreRepositoryProvider.get()
                            .saveInternalServerError(isError = true)
                        }
                    }
                }
            }
        } else {


        }

        Log.d("4444", " finish responseMain?.code=" + responseMain?.code)
        Log.d("4444", " finish responseMain?.body=" + responseMain?.body)
        Log.d("4444", " finish responseMain?.message=" + responseMain?.message)

        responseMain?.let {
            return it
        } ?: run {
            Log.d("4444", " концовка концовка responseMain?.body?.string()=" + responseMain?.body?.string())
            return chain.proceed(chain.request()) // запрос в цепочку обработки запросов без изменений без токена
        }
    }

    private fun badResponse(chain: Interceptor.Chain, httpCode: Int, message: String): Response {
        Log.d("4444", "NetworkModule badResponse выполнился")
        return Response.Builder().code(httpCode).message(message).protocol(Protocol.HTTP_1_1).request(chain.request()).build()
    }

    private fun chainContinueRequest(
        accessToken: String,
        chain: Interceptor.Chain,
    ): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }

    private fun updateTokensRequest(userTokensDetails: UserTokensDetails): Response {
        val gson = Gson()
        val okHttpClient = OkHttpClient()
        val json = gson.toJson(userTokensDetails)
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = BuildConfig.API_URL_HTTP + Constants.PART_URL_UPDATE_TOKENS
        val request = Request.Builder()
            //.addHeader("Authorization", "Bearer $token") // тут не требуется
            .url(url)
            .post(body)
            .build()
        return okHttpClient.newCall(request).execute()
    }

    private fun navigateToAuthScreen(context: Context) {

//        val scope = CoroutineScope(Dispatchers.Main)
//        scope.launch {
//            val deepLink = Uri.parse("auth_screen")
//            val taskDetailIntent = Intent(
//                Intent.ACTION_VIEW,
//                deepLink,
////                    this,
////                    MainScreensActivity::class.java
//                //MainActivity::class.java
//            )
//
//            val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
//                addNextIntentWithParentStack(taskDetailIntent)
//                // addParentStack(MainScreensActivity::class.java)
//                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
//            }
//            pendingIntent.send()
//        }
    }
}