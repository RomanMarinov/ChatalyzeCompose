package com.dev_marinov.chatalyze.data.call.remote

import com.dev_marinov.chatalyze.data.auth.dto.MessageResponseDTO
import com.dev_marinov.chatalyze.data.call.dto.UserCallDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CallApiService {
    @POST("make_call")
    suspend fun makeCall(@Body userCallDTO: UserCallDTO) : Response<MessageResponseDTO>
}