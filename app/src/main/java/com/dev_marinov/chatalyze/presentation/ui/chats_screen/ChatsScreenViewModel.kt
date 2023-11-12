package com.dev_marinov.chatalyze.presentation.ui.chats_screen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository
) : ViewModel() {

    private var _performRequestPermissions: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val performRequestPermissions: StateFlow<Boolean> = _performRequestPermissions


    /////////////
    private var ownPhoneSenderCorrectFormat = ""
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst() // удаляет первый элемент из списка
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        // если разрешение запрешено и оно отсутствует в списке
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    //////////////

    private var _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(listOf())
    val contacts: StateFlow<List<Contact>> = _contacts

    fun onClickHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(Constants.HIDE_BOTTOM_BAR, isHide = isHide)
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
}