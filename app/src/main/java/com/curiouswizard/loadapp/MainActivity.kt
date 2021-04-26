package com.curiouswizard.loadapp

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.curiouswizard.loadapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0

    private var url: String? = null
    private var downloadName: String = ""
    private var downloadStatus: String = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Change download URL based on radio button selection
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                binding.glideRadioButton.id -> {
                    url = GLIDE_URL
                    downloadName = getString(R.string.glide_label)
                }
                binding.loadappRadioButton.id ->{
                    url = LOADAPP_URL
                    downloadName = getString(R.string.loadapp_label)
                }
                binding.retrofitRadioButton.id -> {
                    url = RETROFIT_URL
                    downloadName = getString(R.string.retrofit_label)
                }
            }
        }

        binding.customButton.setOnClickListener {
            // Check if user selected any radio button, if not, it displays a Toast message
            if (binding.radioGroup.checkedRadioButtonId != -1)
                download(url!!)
            else
                Toast.makeText(this, R.string.no_selected_radio_button_toast, Toast.LENGTH_SHORT).show()
        }

        createChannel(CHANNEL_ID, getString(R.string.channel_name))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if(intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE && downloadID == id){
                binding.customButton.setState(ButtonState.Completed)

                val query = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

                if (query.moveToFirst()) {
                    when (query.getInt(query.getColumnIndex(DownloadManager.COLUMN_STATUS))){
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            downloadStatus = "Success"
                        }
                        DownloadManager.STATUS_FAILED -> {
                            downloadStatus = "Failed"
                        }
                    }
                }

                notificationManager.sendNotification(context!!, downloadName, downloadStatus)
            }
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID =
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.

        notificationManager.cancelNotifications()
        binding.customButton.setState(ButtonState.Loading)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download status"

            notificationManager = ContextCompat.getSystemService(
                    applicationContext, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}