package me.nathanfallet.uhaconnect.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.nathanfallet.uhaconnect.R
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        StorageService.getInstance(this).sharedPreferences.getString("token", null)?.let {
            CoroutineScope(Job()).launch {
                try {
                    APIService.getInstance(Unit).sendNotificationToken(it, token)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val title = message.notification?.titleLocalizationKey?.let { key ->
            val id = resources.getIdentifier(key, "string", packageName)
            val string = getString(id)
            message.notification?.titleLocalizationArgs?.let {
                string.format(*it)
            } ?: string
        } ?: message.notification?.title
        val body = message.notification?.bodyLocalizationKey?.let { key ->
            val id = resources.getIdentifier(key, "string", packageName)
            val string = getString(id)
            message.notification?.bodyLocalizationArgs?.let {
                string.format(*it)
            } ?: string
        } ?: message.notification?.body
        val notification = NotificationCompat.Builder(this, "default")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.logo_transparent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        NotificationManagerCompat.from(this).notify(Random.nextInt(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("default", "default", importance)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}