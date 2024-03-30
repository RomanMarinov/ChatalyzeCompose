package com.dev_marinov.chatalyze.di

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Provider


class KtorNetworkInterceptor(
    private val context: Context,
    private val preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>
) : HttpClientPlugin<ResponseObserver.Config, KtorNetworkInterceptor> {

    override val key: AttributeKey<KtorNetworkInterceptor> = AttributeKey("KtorNetworkInterceptor")

    override fun install(plugin: KtorNetworkInterceptor, scope: HttpClient) {
        scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
            try {
                if (plugin.hasInternetConnection()) {
                    Log.d("4444", " ЕСТЬ ИНТЕРНЕТ")
                    proceed()
                } else {
                    finish()
                }
            } catch (e: Exception) {
                Log.d("4444", " try catch KtorNetworkInterceptor e=" + e)
            }

        }
    }

    override fun prepare(block: ResponseObserver.Config.() -> Unit): KtorNetworkInterceptor {
        return this
    }

    @SuppressLint("ServiceCast")
    private fun hasInternetConnection(): Boolean {
        var result = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.run {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> { false }
                }
            }
        }
        return result
    }
}