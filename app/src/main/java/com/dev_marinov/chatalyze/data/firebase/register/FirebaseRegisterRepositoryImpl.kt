package com.dev_marinov.chatalyze.data.firebase.register

import android.util.Log
import com.dev_marinov.chatalyze.data.firebase.register.dto.UserFirebaseDTO
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.FirebaseRegisterRepository
import com.dev_marinov.chatalyze.domain.model.firebase.UserFirebase
import javax.inject.Inject

class FirebaseRegisterRepositoryImpl @Inject constructor(
    private val firebaseApiService: FirebaseApiService
) : FirebaseRegisterRepository {
    override suspend fun firebaseRegister(userFirebase: UserFirebase): MessageResponse? {
        Log.d("4444", " FirebaseRegisterRepositoryImpl firebaseRegister userFirebase=" + userFirebase)
        val userFirebaseDTO = UserFirebaseDTO(
            registerSenderPhone = userFirebase.registerSenderPhone,
            firebaseToken = userFirebase.firebaseToken
        )
        val response = firebaseApiService.firebaseRegister(userFirebaseDTO = userFirebaseDTO)
        return response.body()?.mapToDomain()
    }
}