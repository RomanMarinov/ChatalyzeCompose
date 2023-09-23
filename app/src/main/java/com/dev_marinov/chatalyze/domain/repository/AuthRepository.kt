package com.dev_marinov.chatalyze.domain.repository

interface AuthRepository {
    suspend fun registerUser(login: String, password: String, email: String)
    suspend fun signInUser(login: String, password: String)
    suspend fun sendEmail(email: String)
}