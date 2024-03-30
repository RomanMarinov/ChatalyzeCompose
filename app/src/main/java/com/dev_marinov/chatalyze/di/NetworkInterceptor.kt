package com.dev_marinov.chatalyze.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.InternetConnectionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import java.io.IOException
import javax.inject.Provider

class NetworkInterceptor(
    private val context: Context,
    private val preferencesDataStoreRepositoryProvider: Provider<PreferencesDataStoreRepository>,
) : Interceptor, ConnectivityManager.NetworkCallback() {

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val isHasConnection = InternetConnectionHelper.execute(context = context)
        return if (isHasConnection) {
            chain.proceed(chain.request())
        } else {
            badResponse(chain = chain, httpCode = 504, message = "No internet connection")
        }
    }

    private fun badResponse(chain: Interceptor.Chain, httpCode: Int, message: String): Response {
        Log.d("4444", "NetworkInterceptor badResponse выполнился")
        return Response.Builder().code(httpCode).message(message).protocol(Protocol.HTTP_1_1).request(chain.request()).build()
    }
}