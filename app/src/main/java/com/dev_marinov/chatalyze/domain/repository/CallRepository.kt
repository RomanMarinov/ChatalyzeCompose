package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.FirebaseCommand
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen.model.HistoryCall
import kotlinx.coroutines.flow.Flow

interface CallRepository {
    suspend fun sendCommandToFirebase(firebaseCommand: FirebaseCommand): MessageResponse?

    val getHistoryCalls: Flow<List<HistoryCall>>
    suspend fun deleteHistoryCalls()
    suspend fun saveHistoryCalls(historyCall: HistoryCall)
}