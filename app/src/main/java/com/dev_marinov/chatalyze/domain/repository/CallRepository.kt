package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.call_screen.model.UserCall

interface CallRepository {
    suspend fun makeCall(userCall: UserCall): MessageResponse?
}