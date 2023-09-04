package com.dev_marinov.chatalyze.data

import com.dev_marinov.chatalyze.data.model.auth.RegisterRequest
import com.dev_marinov.chatalyze.data.model.auth.SignInRequest
import okio.FileNotFoundException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<String>

    @POST("login")
    suspend fun signInUser(@Body signInRequest: SignInRequest): Response<String>
}