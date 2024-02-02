package com.dev_marinov.chatalyze.presentation.util

import android.content.Context
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User

object ClientStreamVideo {

    private var client: StreamVideo? = null

    fun getClient(context: Context, user: User, userToken: String) : StreamVideo {
        if (client == null) {
            client = StreamVideoBuilder(
                context = context,
                apiKey = "v8rmt3faerck", // demo API key
                geo = GEO.GlobalEdgeNetwork,
                user = user,
                token = userToken
            ).build()
        }
        return client as StreamVideo


    }
}

// шаг 2 — инициализируем StreamVideo.
// Для производственного приложения мы рекомендуем добавить клиент в класс приложения или модуль di.
//    val client: StreamVideo = StreamVideoBuilder(
//        context = context,
//        apiKey = "v8rmt3faerck", // demo API key
//        geo = GEO.GlobalEdgeNetwork,
//        user = user,
//        token = userToken,
//    ).build()