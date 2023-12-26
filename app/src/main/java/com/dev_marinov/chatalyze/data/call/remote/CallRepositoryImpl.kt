package com.dev_marinov.chatalyze.data.call.remote

import android.util.Log
import com.dev_marinov.chatalyze.data.call.dto.UserCallDTO
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.presentation.ui.call_screen.model.UserCall
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(
    private val callApiService: CallApiService) : CallRepository {
    override suspend fun makeCall(userCall: UserCall): MessageResponse? {
        return try {
            val userCallDto = UserCallDTO(
                event = userCall.event,
                sender = userCall.sender,
                recipient = userCall.recipient
            )
            val response = callApiService.makeCall(userCallDTO = userCallDto)
            return response.body()?.mapToDomain()

        } catch (e: Exception) {
            Log.d("4444", " try catch makeCall e=" + e)
            null
        }
    }


}