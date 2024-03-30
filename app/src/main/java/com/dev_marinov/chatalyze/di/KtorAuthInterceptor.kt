package com.dev_marinov.chatalyze.di

import android.util.Log
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import kotlinx.coroutines.flow.first
import javax.inject.Provider

class KtorAuthInterceptor(
    private val authRepositoryProvider: Provider<AuthRepository>,
    private val preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>
) : HttpClientPlugin<ResponseObserver.Config, KtorAuthInterceptor> {

    override val key: AttributeKey<KtorAuthInterceptor> = AttributeKey("NewAuthInterceptor")

    override fun install(plugin: KtorAuthInterceptor, scope: HttpClient) {
        scope.requestPipeline.intercept(HttpRequestPipeline.Render) { response ->

            Log.d("4444", " qwe NewAuthInterceptor res=" + context.url.build())
            if (context.url.toString().contains(Constants.PART_URL_MESSAGES)) {
                val refreshToken = authRepositoryProvider.get().getRefreshTokensFromDataStore.first()
                val accessToken = authRepositoryProvider.get().getAccessTokensFromDataStore.first()
                if (accessToken.isNotEmpty()) {
                    // val accessToken = "wefwefwefwef"
                    context.headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
        }

        scope.responsePipeline.intercept(HttpResponsePipeline.After) { response ->
            if (context.response.status.value == HttpStatusCode.Unauthorized.value) {
                Log.d("4444", " qwe NewAuthInterceptor Unauthorized response=" + context.request.url + " code=" + context.response.status.value)
                preferencesDataStoreRepositoryProvider.get().saveStateUnauthorized(isUnauthorized = true)
                finish()
            } else {
                Log.d("4444", " qwe NewAuthInterceptor some 1 response=" + context.request.url + " code=" + context.response.status.value)
                proceed()
            }
            Log.d("4444", " qwe NewAuthInterceptor some 2 response=" + context.request.url + " code=" + context.response.status.value)
        }
    }

    override fun prepare(block: ResponseObserver.Config.() -> Unit): KtorAuthInterceptor {
        return this
    }
}
