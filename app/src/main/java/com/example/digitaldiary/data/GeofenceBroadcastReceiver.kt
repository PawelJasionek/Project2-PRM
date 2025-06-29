package com.example.digitaldiary.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.digitaldiary.MainActivity
import com.example.digitaldiary.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import logCurrentLocation

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("GeofenceReceiver", "Intent received")
        logCurrentLocation(context);

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {
            Log.e("GeofenceReceiver", "GeofencingEvent is null")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorCode = geofencingEvent.errorCode
            Log.e("GeofenceReceiver", "Geofencing error code: $errorCode")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        Log.d("GeofenceReceiver", "Transition type: $geofenceTransition")

        if (geofenceTransition == GEOFENCE_TRANSITION_ENTER) {
            val triggering = geofencingEvent.triggeringGeofences
            Log.d("GeofenceReceiver", "Triggering geofences: $triggering")
            showNotification(context, "Near one of the entries!", "Visiting a place from the Diary.")
        } else {
            Log.d("GeofenceReceiver", "Ignored transition type: $geofenceTransition")
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        Log.d("GeofenceReceiver", "Show notification triggered!")
        val channelId = "diary_geofence"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Geofence Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifications for geofence triggers"
            manager.createNotificationChannel(channel)

        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(1001, notification)
    }
}
