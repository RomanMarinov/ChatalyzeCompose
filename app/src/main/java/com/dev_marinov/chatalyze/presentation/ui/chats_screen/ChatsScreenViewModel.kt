package com.dev_marinov.chatalyze.presentation.ui.chats_screen

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.Constants
import com.dev_marinov.chatalyze.presentation.util.CorrectNumberFormatHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatalyzeRepository: ChatsRepository,
) : ViewModel() {

    private var _performRequestPermissions: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val performRequestPermissions: StateFlow<Boolean> = _performRequestPermissions

    private var _chatList = MutableStateFlow<List<Chat>>(listOf())
    val chatList: StateFlow<List<Chat>> = _chatList

    /////////////
    private var ownPhoneSenderCorrectFormat = ""
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun getChats(sender: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response =
                chatalyzeRepository.getChats(sender = CorrectNumberFormatHelper.getCorrectNumber(
                    number = sender))
            Log.d("4444", " ChatsScreenViewModel response=" + response)


            _chatList.value = response
            //_chatList.value = response + getFakeChats()
        }
    }

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst() // удаляет первый элемент из списка
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean,
    ) {
        // если разрешение запрешено и оно отсутствует в списке
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    //////////////

    private var _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(listOf())
    val contacts: StateFlow<List<Contact>> = _contacts

    fun onClickHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(Constants.HIDE_BOTTOM_BAR,
                isHide = isHide)
        }
    }

    fun transferContacts(contacts: List<Contact>) {
        _contacts.value = contacts

//        val contacts2 = listOf(Contact(
//            name = "АртурМег",
//            phoneNumber = "9203333333",
//            photo = ""
//        ))
        //  _contacts.value = contacts2
    }

    fun makeRequestPermissions(perform: Boolean) {
        _performRequestPermissions.value = perform
    }

    private fun getFakeChats(): List<Chat> {
        return listOf(
            Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test1",
                createdAt = "120"
            ),
            Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test2",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test3",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test4",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test5",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test6",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test7",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test8",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test9",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test10",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test11",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test12",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test13",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test14",
                createdAt = "120"
            ), Chat(
                sender = "1234567891",
                recipient = "1234567891",
                textMessage = "test15",
                createdAt = "120"
            ))
    }

    fun saveOwnPhoneSender(ownPhoneSender: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("4444", " saveOwnPhoneSender ownPhoneSender=" + ownPhoneSender)
            preferencesDataStoreRepository.saveOwnPhoneSender(
                key = Constants.OWN_PHONE_SENDER,
                ownPhoneSender = ownPhoneSender)
        }
    }
}