package ru.amaderu.permissions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(
    private val contacts: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {
    // Создание нового ViewHolder при необходимости
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
//        cont = parent.context // Получение контекста из родительского ViewGroup
        //based on android.R.layout.simple_list_item_2
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ContactViewHolder(view)
    }
    // Связывает данные с элементом списка в заданной позиции и обновляет его содержимое
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
        holder.itemView.setOnClickListener { onItemClick(contact) }
    }

    // Возвращаем общее количество элементов в списке
    override fun getItemCount(): Int = contacts.size

    // ViewHolder для элементов списка
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1: TextView = itemView.findViewById(R.id.tv_FIO)
        private val text2: TextView = itemView.findViewById(R.id.tv_listed)

        fun bind(contact: Contact) {
            text1.text = contact.name
            text2.text = contact.listed
//            text2.text = contact.getFormattedPhones()
        }
    }
}