package ru.amaderu.permissions

import android.content.Context
import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract

object ContactsHelper {

    fun getContacts(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver

        // Определяем какие колонки будем запрашивать
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.LOOKUP_KEY
        )

        // Сортируем по имени
        val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC"

        // Запрашиваем контакты
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(ContactsContract.Contacts._ID)
            val nameColumn = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val lookupKeyColumn = it.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)


            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val lookupKey = it.getString(lookupKeyColumn)
//                val phones = getPhones(contentResolver, id)
                val nickname = getNickname(contentResolver, lookupKey).orEmpty() // Получаем nickname

                if (name != null) {
                    contacts.add(Contact(id, name, nickname))
//                    contacts.add(Contact(id, name, phones, lookupKey))
                }

            }
        }

        return contacts
    }

    private fun getPhones(contentResolver: ContentResolver, contactId: Long): List<String> {
        val phones = mutableListOf<String>()

        val phoneCursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )

        phoneCursor?.use {
            val numberColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val phoneNumber = it.getString(numberColumn)
                if (phoneNumber != null) {
                    phones.add(phoneNumber)
                }
            }
        }

        return phones
    }

    // Функция для получения nickname контакта
    private fun getNickname(contentResolver: ContentResolver, lookupKey: String): String? {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Nickname.NAME
        )

        contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            "${ContactsContract.Data.LOOKUP_KEY} = ? AND " +
                    "${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE}'",
            arrayOf(lookupKey),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(0)
            }
        }
        return null
    }
}