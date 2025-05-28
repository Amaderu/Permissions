package ru.amaderu.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactsAdapter
    private lateinit var contactsTextView: TextView
    private val contactsList = mutableListOf<Contact>()

    companion object {
        private const val PERMISSION_REQUEST_READ_CONTACTS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val getContactsButton: Button = findViewById(R.id.btn_getContactsButton)
        contactsTextView = findViewById(R.id.contactsTextView)


        getContactsButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Разрешение не предоставлено, начинаем процесс запроса разрешений
                showSettingsDialog(this@MainActivity)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSION_REQUEST_READ_CONTACTS
                )
            } else {
                // Разрешение уже предоставлено, получаем контакты
                loadContacts()
            }
        }
        recyclerView = findViewById(R.id.contacts_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = ContactsAdapter(contactsList) { contact ->
            // Обработка клика по контакту
            Toast.makeText(this@MainActivity, "Selected: ${contact.name}", Toast.LENGTH_SHORT)
                .show()
        }
        recyclerView.adapter = adapter


    }

    private fun loadContacts() {
        val contacts = ContactsHelper.getContacts(this@MainActivity)
        contactsList.clear()
        contactsList.addAll(contacts)
        adapter.notifyDataSetChanged()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, получаем контакты
                val contacts = ContactsHelper.getContacts(this@MainActivity)
                loadContacts()
            } else {
                // Разрешение не получено, показываем информацию пользователю
                Toast.makeText(
                    this, "Без разрешения на доступ к контактам функционал ограничен",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, PermissionDeniedActivity::class.java))
            }
        }
    }

    private fun createSettingsIntent() {
        // Даем пользователю возможность перейти в системные настройки приложения
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }


    private fun showSettingsDialog(context: Context) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(null)
        // Для Android 5.0 (API 21) и выше
        val icon = ContextCompat.getDrawable(context, R.drawable.warning_24dp)?.apply {
            //setTint(getColor(R.color.red))
            // старый способ, но работает на многих версиях Android
            colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        }
        builder.setIcon(icon)
        /*
        //Custom Инициализация макета диалога
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_contacts, null)
        builder.setView(dialogLayout)
        */
        builder.setMessage(getString(R.string.dialog_text1))
        // Настройка кнопок диалога
        builder.setPositiveButton(getString(R.string.btn_text_open_settings_pos)) { dialog, _ ->
            createSettingsIntent()
        }
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            revokeSelfPermissionOnKill(Manifest.permission.READ_CONTACTS)
        }
    }


}


