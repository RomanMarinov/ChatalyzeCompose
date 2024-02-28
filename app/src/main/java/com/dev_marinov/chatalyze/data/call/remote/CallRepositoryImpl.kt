package com.dev_marinov.chatalyze.data.call.remote

import android.util.Log
import com.dev_marinov.chatalyze.data.call.dto.FirebaseCommandDTO
import com.dev_marinov.chatalyze.data.call.local.HistoryCallsDao
import com.dev_marinov.chatalyze.data.call.local.HistoryCallsEntity
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.FirebaseCommand
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen.model.HistoryCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(
    private val callApiService: CallApiService,
    private val historyCallsDao: HistoryCallsDao
    ) : CallRepository {

    override suspend fun sendCommandToFirebase(firebaseCommand: FirebaseCommand): MessageResponse? {
        return try {
            val firebaseCommandDTO = FirebaseCommandDTO(
                topic = firebaseCommand.topic,
                senderPhone = firebaseCommand.senderPhone,
                recipientPhone = firebaseCommand.recipientPhone,
                textMessage = firebaseCommand.textMessage,
                typeFirebaseCommand = firebaseCommand.typeFirebaseCommand
            )
           // Log.d("4444", " CallRepositoryImpl userCallDto=" + firebaseCommandDTO)
            val response = callApiService.sendCommandToFirebase(firebaseCommandDTO = firebaseCommandDTO)
            return response.body()?.mapToDomain()
        } catch (e: Exception) {
            Log.d("4444", " try catch makeCall e=" + e)
            null
        }
    }

    override val getHistoryCalls: Flow<List<HistoryCall>> = historyCallsDao.getAllFlow().map {
        it.map {  historyCallsEntity ->
            HistoryCall(
                clientCallPhone = historyCallsEntity.clientCallPhone,
                senderPhone = historyCallsEntity.senderPhone,
                recipientPhone = historyCallsEntity.recipientPhone,
                conversationTime = historyCallsEntity.conversationTime,
                createdAt = historyCallsEntity.createdAt
            )
        }
    }

    override suspend fun deleteHistoryCalls() {
        historyCallsDao.deleteAll()
    }

    override suspend fun saveHistoryCalls(historyCall: HistoryCall) {
        val historyCallsEntity = HistoryCallsEntity(
            id = 0,
            clientCallPhone = historyCall.clientCallPhone,
            senderPhone = historyCall.senderPhone,
            recipientPhone = historyCall.recipientPhone,
            conversationTime = historyCall.conversationTime,
            createdAt = historyCall.createdAt
        )
        Log.d("4444", " saveHistoryCalls historyCallsEntity=" + historyCallsEntity)
        historyCallsDao.insert(historyCallsEntity = historyCallsEntity)
    }
}