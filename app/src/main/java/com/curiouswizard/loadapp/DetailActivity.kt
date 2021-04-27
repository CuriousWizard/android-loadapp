package com.curiouswizard.loadapp

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.curiouswizard.loadapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dismissing the notification
        val notificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()

        // Using View binding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Getting informations from the
        intent.getStringExtra("name").let {
            binding.fileName.text = it
        }
        intent.getStringExtra("status").let {
            binding.status.text = it
        }

        // Setting up OK button
        binding.confirmButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}