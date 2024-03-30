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
import com.dev_marinov.chatalyze.presentation.util.TokenTime
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.http.HTTP_FORBIDDEN
import okhttp3.internal.http.HTTP_OK
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import org.json.JSONObject
import javax.inject.Provider

class AuthInterceptorFirst(
    private val context: Context,
    private val authRepositoryProvider: Provider<AuthRepository>,
    private val preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>,
) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("4444", " NetworkModule intercept выполнился")
        var responseMain: Response? = null

        val currentUrl = chain.request().url.toString()
        if (
            //currentUrl.contains(Constants.PART_URL_LOGOUT, true) ||
            //currentUrl.contains(Constants.PART_SEND_MESSAGE, true) ||
            //currentUrl.contains(Constants.PART_CHAT_COMPANION, true) ||
            currentUrl.contains(Constants.PART_URL_CHATS, true)
//            currentUrl.contains(Constants.PART_URL_DELETE_PROFILE, true)
        ) {
            runBlocking {
                val refreshToken: String? = authRepositoryProvider.get().getRefreshTokensFromDataStore.firstOrNull()
                refreshToken?.let { refreshT ->
                    val refreshTokenPayload: TokenPayload = DecodeToken.execute(token = refreshT)
                    refreshTokenPayload.expiresIn?.let { expiresIn ->
                        val isExpiredRefreshToken = TokenTime.getExpiredStatus(expiresIn = expiresIn)
                        if (!isExpiredRefreshToken) {  // refreshToken не истек
                            Log.d("4444", " NetworkModule refreshToken не истек, чекаем дальше accessToken")
                            val accessToken: String? = authRepositoryProvider.get().getAccessTokensFromDataStore.firstOrNull()
                            accessToken?.let { accessT ->
                                val accessTokenPayload: TokenPayload = DecodeToken.execute(token = accessT)
                                accessTokenPayload.expiresIn?.let { expiresIn ->
                                    val isExpiredAccessToken = TokenTime.getExpiredStatus(expiresIn = expiresIn)
                                    if (isExpiredAccessToken) { // accessToken истек надо обновить пару
                                        Log.d("4444", " NetworkModule accessToken истек, выполняем обновление")
                                        runBlocking {
                                            async {
                                                val userTokensDetails = UserTokensDetails(
                                                    accessToken = accessT,
                                                    refreshToken = refreshT,
                                                    userId = refreshTokenPayload.userId
                                                )
                                                Log.d("4444", " проверка userTokensDetails=" + userTokensDetails)
                                                val response: Response = updateTokensRequest(userTokensDetails = userTokensDetails)

                                                response.body.string().let {
                                                    when(response.code) {
                                                        HTTP_OK -> {
                                                            Log.d("4444", " говно 1")
                                                            // тут выполняется все код который выполнился удачно на сервере даже если
                                                            // вернул ошибку с токеном или что-то подобное
                                                            // чтобы не заркылся респонс
                                                            if (it.contains("\"httpStatusCode\"") && it.contains("\"message\"")) {
                                                                val jsonResponse2 = JSONObject(it)
                                                                val httpStatusCode = jsonResponse2.optString("httpStatusCode")
                                                                val message = jsonResponse2.optString("message")

                                                                when(httpStatusCode.toInt()) {
                                                                    HTTP_OK -> {
                                                                        Log.d("4444", " на втором ярусе тут сработал HTTP_OK")
                                                                        // вернуть полученый четкий респонс или обект наверно
                                                                        val jsonResponse = JsonParser.parseString(it)
                                                                        val pairOfTokens = Gson().fromJson(jsonResponse, PairOfTokens::class.java)
                                                                        authRepositoryProvider.get().savePairTokens(pairOfTokens = PairOfTokens(
                                                                            accessToken = pairOfTokens.accessToken,
                                                                            refreshToken = pairOfTokens.refreshToken)
                                                                        )
                                                                        val responseChain: Response = chainCopyRequest(accessToken = pairOfTokens.accessToken, chain = chain)
                                                                        responseMain = responseChain
                                                                    }
                                                                    HTTP_FORBIDDEN -> {
                                                                        Log.d("4444", " на втором ярусе тут сработал HTTP_FORBIDDEN")
                                                                        responseMain = badResponse(chain = chain, httpCode = HTTP_FORBIDDEN, message = message)
                                                                    } HTTP_UNAUTHORIZED -> { // или refreshToken пуст или не совпадает
                                                                    Log.d("4444", " на втором ярусе тут сработал HTTP_UNAUTHORIZED")
                                                                        responseMain = badResponse(chain = chain, httpCode = HTTP_UNAUTHORIZED, message = message)

                                                                     //   navigateToAuthScreen(context = context)
                                                                    } else -> {
                                                                    Log.d("4444", " на втором ярусе тут сработал else")
                                                                        preferencesDataStoreRepositoryProvider.get().saveFailureUpdatePairToken(isFailure = true)
                                                                    }
                                                                }
                                                            } else {
                                                                Log.d("4444", " NetworkModule полей httpStatusCode и message не обнаружено")
                                                            }
                                                        } else -> {
                                                            ///////////////////////
                                                        val repeatResponse = updateTokensRequest(userTokensDetails = userTokensDetails)
                                                        repeatResponse.body.string().let { repeatResp ->
                                                            when(repeatResponse.code) {
                                                                HTTP_OK -> {
                                                                    //////////////////////
                                                                    // repeatResponse.body.close()
                                                                    // праильно распарсить респонсе чтобы получить соде
                                                                    if (repeatResp.contains("\"httpStatusCode\"")
                                                                        && repeatResp.contains("\"message\"")) {

                                                                        val jsonRepeatResponse = JSONObject(repeatResp)
                                                                        val repeatHttpStatusCode = jsonRepeatResponse.optString("httpStatusCode")
                                                                        val repeatMessage = jsonRepeatResponse.optString("message")
                                                                        when (repeatHttpStatusCode.toInt()) {
                                                                            HTTP_OK -> {
                                                                                val pairOfTokens = Gson().fromJson(
                                                                                        repeatResponse.body.string(),
                                                                                        PairOfTokens::class.java
                                                                                    )
                                                                                authRepositoryProvider.get().savePairTokens(pairOfTokens = PairOfTokens(
                                                                                            accessToken = pairOfTokens.accessToken,
                                                                                            refreshToken = pairOfTokens.refreshToken
                                                                                        )
                                                                                    )
                                                                                val responseChain: Response = chainCopyRequest(accessToken = pairOfTokens.accessToken, chain = chain)
                                                                                responseMain = responseChain
                                                                            }

                                                                            HTTP_FORBIDDEN, HTTP_UNAUTHORIZED -> {
                                                                                //chain.call().cancel()
                                                                                Log.d("4444", " тут сработал cancel() 2")
                                                                                preferencesDataStoreRepositoryProvider.get()
                                                                                    .saveStateNotFoundRefreshToken(
                                                                                        isNotFound = true
                                                                                    )
                                                                                navigateToAuthScreen(context = context)
                                                                            } else -> { // тут неудачный ответ после второй попытки обновить токен
                                                                            preferencesDataStoreRepositoryProvider.get().saveFailureUpdatePairToken(isFailure = true)
                                                                             }
                                                                        }
                                                                    } else {
                                                                        Log.d("4444", " NetworkModule полей httpStatusCode и message не обнаружено")
                                                                    }
                                                                } else -> {
                                                                    preferencesDataStoreRepositoryProvider.get().saveFailureUpdatePairToken(isFailure = true)
                                                                  }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Log.d("4444", " NetworkModule accessToken не истек, выполняем отправку accessToken")
                                        val responseChain: Response = chainCopyRequest(accessToken = accessT, chain = chain)
                                        responseMain = responseChain
                                    }
                                }
                            }
                        } else {  // refreshToken истек
                            Log.d("4444", " NetworkModule refreshToken истек")
                           // chain.call().cancel()
                            Log.d("4444", " тут сработал cancel() 3")
                            navigateToAuthScreen(context = context)
                        }
                    }
                }
            }
        }

//        почему то не вижу как идет запрос по урл чатс
//        тут проверить в логах responseMain
//
//
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
//        val response: Response? = null
//        try {
//
//            // Ваша логика для формирования responseMain
//        } catch (e: Exception) {
//            Log.e("4444", "Ошибка при формировании responseMain: ${e.message}")
//            // В случае ошибки, верните что-то другое, например, ошибочный Response
//            return Response.Builder()
//                .code(httpCode)
//                .message(message)
//                .protocol(Protocol.HTTP_1_1)
//                .request(chain.request())
//                .build()
//        }
//
//        // Если удалось сформировать responseMain, верните его
        return Response.Builder().code(httpCode).message(message).protocol(Protocol.HTTP_1_1).request(chain.request()).build()
    }

    private fun chainCopyRequest(
        accessToken: String,
        chain: Interceptor.Chain,
    ) : Response {
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