package com.dev_marinov.chatalyze.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        Available, UnAvailable, Losing, Lost
    }
}

@Singleton
class NetworkObserverHelper @Inject constructor(context: Context) : ConnectivityObserver {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // если сворачиваем или переходим на другой экран то перестает налюдать за статусами подключения
    // и будет объявлено о закрытии awaitClose и отменяем регистрацию по сети
    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityObserver.Status.Available) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(ConnectivityObserver.Status.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectivityObserver.Status.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(ConnectivityObserver.Status.UnAvailable) }
                }
            }

            // регистрируем обратный вызов по сети
            connectivityManager.registerDefaultNetworkCallback(callback)
            // ождание закрытия и если будет запущено во viewModel то это будет вызвано по закрытию viewModel

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged() // не приведет к срабатыванию если будет одно и тоже состояние сети а только если разные
    }
}