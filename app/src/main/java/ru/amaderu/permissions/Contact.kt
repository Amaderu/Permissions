package ru.amaderu.permissions

data class Contact(
    val id: Long,
    var name: String,
    var listed: String,//nickname
//    val phones: List<String>,
//    val lookupKey: String
){
    // Форматированное отображение телефонов
//    fun getFormattedPhones(): String {
//        return phones.joinToString(", ")
//    }
}