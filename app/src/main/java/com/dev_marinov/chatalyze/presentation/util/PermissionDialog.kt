package com.dev_marinov.chatalyze.presentation.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionAlertDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = { Text(text = "Требуются разрешения") },
        text = {
            Text(
                text = permissionTextProvider.getDescription(isPermanentlyDeclined = isPermanentlyDeclined)
            )
        },
        buttons = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider()
                Text(
                    text = if (isPermanentlyDeclined) {
                        "Принять разрешения"
                    } else {
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class ContactPermissionTextProvider(
    private val permission_read_contact_true: String,
    private val permission_read_contact_false: String
) : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            permission_read_contact_true
//            "Похоже, вы навсегда отказали в разрешении контакты»." + "Вы можете перейти в настройки приложения, чтобы предоставить его"
        } else {
            permission_read_contact_false
//            "Этому приложению нужен доступ к вашим контактам, чтобы вы имели возможность написать друзьям"
        }
    }
}

class RecordAudioPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Похоже, вы навсегда отклонили разрешение на использование микрофона " + "Вы можете перейти в настройки приложения, чтобы предоставить его"
        } else {
            "Этому приложению нужен доступ к вашему микрофону, чтобы ваши друзья " + "слышу вас во время разговора"
        }
    }
}

class PhoneCallPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Похоже, вы навсегда отказались от разрешения на телефонный звонок " + "Вы можете перейти в настройки приложения, чтобы предоставить его"
        } else {
            "Этому приложению требуется разрешение на телефонные звонки, чтобы вы могли разговаривать " + "Твоим друзьям"
        }
    }
}

class ReadPhoneNumberPermissionTextProvider(
    private val permission_read_phone_numbers_true: String,
    private val permission_read_phone_numbers_false: String
) : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            permission_read_phone_numbers_true
//            "Похоже, вы навсегда отказались от разрешения на доступ к вашему номеру телефона " + "Вы можете перейти в настройки приложения, чтобы предоставить его"
        } else {
            permission_read_phone_numbers_false
  //          "Этому приложению требуется разрешение на получение телефонного номера, чтобы вы могли разговаривать " + "Твоим друзьям"
        }
    }
}