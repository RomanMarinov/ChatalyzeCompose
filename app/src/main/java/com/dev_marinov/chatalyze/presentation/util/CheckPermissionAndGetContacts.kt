package com.dev_marinov.chatalyze.presentation.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


@Composable
fun CheckPermissionAndGetContacts(): Boolean {
    val context = LocalContext.current
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED
}

@SuppressLint("Range", "Recycle")
@Composable
fun RememberContacts(context: Context): Flow<List<Contact>> = flow {

    val contacts = mutableListOf<Contact>()
    val cursor = context.contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        null,
        null,
        null,
        null
    )

    if ((cursor?.count ?: 0) > 0) {
        while (cursor != null && cursor.moveToNext()) {
            // Извлечение данных о контакте
            val id: String =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val name: String =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                val pCur: Cursor? = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id),
                    null
                )
                while (pCur != null && pCur.moveToNext()) {
                    // Извлечение данных о номере телефона и фотографии
                    val phoneNo: String =
                        pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val photo: String? =
                        pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))

                    contacts.add(
                        Contact(name = name, phoneNumber = CorrectNumberFormatHelper.getCorrectNumber(phoneNo), photo = photo)
                    )
                }
                pCur?.close()
            }
        }
    }

    cursor?.close()

    emit(contacts)
}