package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.stream_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen.getCurrentDateTimeString
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen.model.HistoryCall
import com.dev_marinov.chatalyze.presentation.util.CustomDateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamScreenViewModel @Inject constructor(
    private val callRepository: CallRepository,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
) : ViewModel() {

    private val getOwnPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender
    var ownPhoneSender = ""

    private var jobCallingLoop: Job? = null

    private var currentTimeUnix: Long = 0L

    init {
        saveLocalOwnPhoneSender()
    }

    private fun saveLocalOwnPhoneSender() {
        viewModelScope.launch(Dispatchers.IO) {
            getOwnPhoneSender.collect {
                ownPhoneSender = it
            }
        }
    }

    fun saveHistoryCalls(recipientPhone: String, senderPhone: String, clientCallPhone: String) {
        val dateTimeString = getCurrentDateTimeString()
        val createdAt = CustomDateTimeHelper.formatDateTime(dateTimeString)
        val historyCall = HistoryCall(
            clientCallPhone = clientCallPhone,
            senderPhone = senderPhone,
            recipientPhone = recipientPhone,
            conversationTime = "очень долго",
            createdAt = createdAt
        )
        viewModelScope.launch(Dispatchers.IO) {
            callRepository.saveHistoryCalls(historyCall = historyCall)
        }
    }
}