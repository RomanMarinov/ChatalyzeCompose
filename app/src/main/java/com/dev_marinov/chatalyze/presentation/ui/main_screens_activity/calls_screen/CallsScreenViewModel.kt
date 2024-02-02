package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.calls_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.call.HistoryCallWithName
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CallsScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val callRepository: CallRepository,
    private val roomRepository: RoomRepository,
) : ViewModel() {

    val pushTypeDisplay = preferencesDataStoreRepository.pushTypeDisplayFlow
    var pushTypeDisplayLocal = 0

    private val historyCallList = callRepository.getHistoryCalls
    val filteredContacts = roomRepository.filteredContacts
    private var _historyCallsCombine: MutableStateFlow<List<HistoryCallWithName>> = MutableStateFlow(emptyList())
    val historyCallsCombine: StateFlow<List<HistoryCallWithName>> = _historyCallsCombine

//    private var _HistoryCallList: MutableStateFlow<List<HistoryCall>> = MutableStateFlow(listOf())
//    val historyCallList: StateFlow<List<HistoryCall>> = _HistoryCallList

    val isSessionState = preferencesDataStoreRepository.isSessionState

    private var _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(listOf())
    val contacts: StateFlow<List<Contact>> = _contacts

    private var _isOpenModalBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOpenModalBottomSheet: StateFlow<Boolean> = _isOpenModalBottomSheet

    private var _getChatListFlag: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val getChatListFlag: StateFlow<Boolean> = _getChatListFlag

    private var _makeCallStatusCode: MutableStateFlow<Int> = MutableStateFlow(0)
    val makeCallStatusCode: StateFlow<Int> = _makeCallStatusCode

    val getOwnPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender
    var ownPhoneSenderLocal = ""

//    fun getFakeCalls() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = chatRepository.getChatMessage()
//           // _fakeCalls.value = result
//        }
//    }

    init {
        Log.d("4444", " ChatsScreenViewModel сработал init")
        saveLocalOwnPhoneSender()
        saveLocalPushTypeDisplay()

        createCombineHistoryCallsList()
    }

    private fun createCombineHistoryCallsList() {
        val combineHistoryCallsList: Flow<List<HistoryCallWithName>> =
            combine(historyCallList, filteredContacts) { callList, contacts ->
                callList.map { historyCall ->

                    val senderPhoneName = contacts.firstOrNull {
                        historyCall.senderPhone == it.phoneNumber
                    }?.name ?: historyCall.senderPhone

                    val recipientPhoneName = contacts.firstOrNull {
                        historyCall.recipientPhone == it.phoneNumber
                    }?.name ?: historyCall.recipientPhone

                    HistoryCallWithName(
                        clientCallPhone = historyCall.clientCallPhone,
                        senderPhone = historyCall.senderPhone,
                        recipientPhone = historyCall.recipientPhone,
                        senderPhoneName = senderPhoneName,
                        recipientPhoneName = recipientPhoneName,
                        conversationTime = historyCall.conversationTime,
                        createdAt = historyCall.createdAt
                    )
                }
            }
        viewModelScope.launch(Dispatchers.IO) {
            combineHistoryCallsList.collect {
                _historyCallsCombine.value = it
            }

        }
    }

    fun onClickHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(
                Constants.HIDE_BOTTOM_BAR,
                isHide = isHide
            )
        }
    }

    fun openModalBottomSheet(isOpen: Boolean) {
        _isOpenModalBottomSheet.value = isOpen
    }

    // походу из бд надо брать
    fun canGetChatList(can: Boolean) {
        _getChatListFlag.value = can
    }

    // походу из бд надо брать ИЛИ ЭТО
    fun createContactsFlow(contacts: List<Contact>) {
        Log.d("4444", " ChatsScreenViewModel createContactsFlow contacts=" + contacts)
        _contacts.value = contacts
    }

    private fun saveLocalOwnPhoneSender() {
        viewModelScope.launch(Dispatchers.IO) {
            getOwnPhoneSender.collect {
                ownPhoneSenderLocal = it
            }
        }
    }

    private fun saveLocalPushTypeDisplay() {
        viewModelScope.launch(Dispatchers.IO) {
//            pushTypeDisplay.collect {
//             //   pushTypeDisplayLocal = it
//            }
        }
    }

    fun savePushTypeDisplay(selectedBoxIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.savePushTypeDisplay(selectedBoxIndex = selectedBoxIndex)
        }
    }
}