package com.dev_marinov.chatalyze.data.chatMessage

class PingHandler {
    private var lastPingTime: Long = 0

    fun handlePing() {
        lastPingTime = System.currentTimeMillis()
    }

    fun hasTimedOut(): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastPingTime) > PING_TIMEOUT_MS
    }

    companion object {
        private const val PING_TIMEOUT_MS = 5000 // Установите значение тайм-аута подходящее для вашего случая
    }
}