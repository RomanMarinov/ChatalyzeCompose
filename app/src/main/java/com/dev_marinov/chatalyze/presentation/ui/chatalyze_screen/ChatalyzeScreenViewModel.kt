package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatalyzeScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
) : ViewModel() {

    val isGrantedPermissions  = preferencesDataStoreRepository.isGrantedPermissions
    val isTheLifecycleEventNow = preferencesDataStoreRepository.isTheLifecycleEventNow

    val ownPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender
    var phoneSender = ""

    val isHideBottomBar = preferencesDataStoreRepository.getHideBottomBar

    private var _canStartService: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val canStartService: StateFlow<Boolean> = _canStartService

    init {
        savePhoneInViewModel()
    }

    private fun savePhoneInViewModel() {
        viewModelScope.launch(Dispatchers.IO) {
            ownPhoneSender.collect {
                if (it.isNotEmpty()) {
                    phoneSender = it
                }
            }
        }
    }

    fun saveHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(
                Constants.HIDE_BOTTOM_BAR,
                isHide = isHide
            )
        }
    }

    fun saveOwnPhoneSender(ownPhoneSender: String) {
        viewModelScope.launch(Dispatchers.IO) {
          //  Log.d("4444", " saveOwnPhoneSender ownPhoneSender=" + ownPhoneSender)
            preferencesDataStoreRepository.saveOwnPhoneSender(
                key = Constants.OWN_PHONE_SENDER,
                ownPhoneSender = ownPhoneSender
            )
        }
    }

    fun savePermissions(key: String, isGranted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveGrantedPermissions(
                key = key,
                isGranted = isGranted
            )
        }
    }

    fun saveLifecycleEvent(eventType: String) {
        Log.d("4444", " ChatalyzeScreenViewModel saveLifecycleEvent eventType=" + eventType)
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveLifecycleEvent(eventType = eventType)
        }
    }

    fun canStartService(can: Boolean) {
        _canStartService.value = can
    }

    fun saveSessionState(sessionState: String) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveSessionState(sessionState = sessionState)
        }
    }
}