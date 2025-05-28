package ru.amaderu.permissions

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.net.Uri
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.provider.Settings

class PermissionDeniedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_permission_denied)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.permission_DeniedActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Перекрашиваем иконку в красный цвет
        val warningIcon =
            ContextCompat.getDrawable(this, R.drawable.warning_24dp)?.let { original ->
                val coloredIcon = DrawableCompat.wrap(original.mutate())
                DrawableCompat.setTint(coloredIcon, ContextCompat.getColor(this, R.color.red))
                coloredIcon
            }


        val imageView  = findViewById<ImageView>(R.id.warringImageView)
        imageView.setImageDrawable(warningIcon)

        // Настройка текстов
        val dialogTextView = findViewById<TextView>(R.id.dialogTextView)
        val dialogTextView2: TextView = findViewById<TextView>(R.id.dialogTextView2)
        dialogTextView.text = getString(R.string.dialog_text1)
        dialogTextView2.text = getString(R.string.dialog_text2)

        // Обработчик для кнопки "Настройки"
        (findViewById<Button>(R.id.btn_dialog_pos)!!).setOnClickListener {
            openAppSettings()
        }

        // Обработчик для кнопки "Отмена"
        (findViewById<Button>(R.id.btn_dialog_neg)!!).setOnClickListener {
            finish()
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)//Uri.parse("package:$packageName")
        }
        startActivity(intent)
        finish()
    }

}