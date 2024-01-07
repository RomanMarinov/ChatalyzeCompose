package com.dev_marinov.chatalyze.presentation.ui.start_screen_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository
) : ViewModel() {

    fun saveFirebaseToken(firebaseToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveFirebaseToken(firebaseToken = firebaseToken)
        }
    }

}
