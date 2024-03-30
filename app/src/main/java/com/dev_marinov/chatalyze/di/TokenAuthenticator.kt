//package com.dev_marinov.chatalyze.di
//
//import com.dev_marinov.chatalyze.domain.repository.AuthRepository
//import kotlinx.coroutines.runBlocking
//import okhttp3.Authenticator
//import okhttp3.Request
//import okhttp3.Response
//import okhttp3.Route
//import javax.inject.Provider
//
//class TokenAuthenticator(
//    private val authRepositoryProvider: Provider<AuthRepository>,
//    private val tokenManager: TokenManager
//) : Authenticator {
//    override fun authenticate(route: Route?, response: Response): Request {
//        synchronized(this) {
//            val sessionData = if (isRefreshNeeded(response)) {
//                runBlocking { getUpdatedSessionData() }
//            } else {
//                getExistingSessionData()
//            }
//
//            return response.request.newBuilder()
//                .header(HeaderKeys.SESSION_ID, sessionData.sessionId)
//                .header(HeaderKeys.REFRESH_ID, sessionData.refreshId)
//                .build()
//        }
//    }
//
//    private fun isRefreshNeeded(response: Response): Boolean {
//        val oldSessionId = response.request.header(HeaderKeys.SESSION_ID)
//        val oldRefreshId = response.request.header(HeaderKeys.REFRESH_ID)
//
//        val updatedSessionId = tokenManager.getSessionId()
//        val updatedRefreshId = tokenManager.getRefreshId()
//
//        return (oldSessionId == updatedSessionId && oldRefreshId == updatedRefreshId)
//    }
//
//    private fun getExistingSessionData(): ApiResponse.SessionData {
//        val updatedSessionId = tokenManager.getSessionId()
//        val updatedRefreshId = tokenManager.getRefreshId()
//        return ApiResponse.SessionData(
//            sessionId = updatedSessionId,
//            refreshId = updatedRefreshId
//        )
//    }
//
//    private suspend fun getUpdatedSessionData(): ApiResponse.SessionData {
//        val refreshTokenRequest =
//            ApiResponse.RefreshSessionRequest(tokenManager.getRefreshId())
//        return when (val result =
//            getResult { userApiService().refreshSession(refreshTokenRequest) }) {
//            is ApiResult.Success -> {
//                val sessionData = result.data.data
//                tokenManager.saveSessionId(sessionData.sessionId)
//                tokenManager.saveRefreshId(sessionData.refreshId)
//                delay(50)
//                sessionData
//            }
//            is ApiResult.Error -> {
//                MySdk.instance().mySdkListeners?.onSessionExpired()
//                return ApiResponse.SessionData()
//            }
//        }
//    }
//
//    private class CustomNetworkStateChecker : NetworkStateChecker {
//        override fun isNetworkAvailable() = true
//    }
//
//    private fun userApiService(): UserApiService {
//        val retrofit = RetrofitHelper.provideRetrofit(
//            RetrofitHelper.provideOkHttpClient(CustomNetworkStateChecker(), tokenManager)
//        )
//        return retrofit.create(UserApiService::class.java)
//    }
//}