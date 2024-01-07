package com.dev_marinov.chatalyze.data.call.remote

import android.util.Log
import com.dev_marinov.chatalyze.data.call.dto.UserCallDTO
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.UserCall
import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(
    private val callApiService: CallApiService) : CallRepository {

    override suspend fun makeCall(userCall: UserCall): MessageResponse? {
        return try {
            val userCallDTO = UserCallDTO(
                topic = userCall.topic,
                sender = userCall.sender,
                recipient = userCall.recipient
            )
            Log.d("4444", " CallRepositoryImpl userCallDto=" + userCallDTO)
            val response = callApiService.makeCall(userCallDTO = userCallDTO)
            return response.body()?.mapToDomain()

        } catch (e: Exception) {
            Log.d("4444", " try catch makeCall e=" + e)
            null
        }
    }
}