package com.example.digitaldiary.viewmodel

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.example.digitaldiary.data.DiaryEntry
import com.example.digitaldiary.data.GeofenceBroadcastReceiver
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.GeofenceStatusCodes

class GeofenceManager(private val context: Context) {


    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            .apply {
                action = "com.example.digitaldiary.ACTION_GEOFENCE_EVENT"
            }
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    fun registerGeofences(entries: List<DiaryEntry>) {

        val geofences = entries.mapNotNull { entry ->

            if(entry.latitude != null && entry.longitude != null) {
                Geofence.Builder()
                    .setRequestId(entry.id)
                    .setCircularRegion(entry.latitude, entry.longitude, 1000f)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build()
            }else null
        }

        if(geofences.isEmpty()){
            Log.w(TAG, "No valid geofences to register")
            return
        }

        entries.forEach { Log.d("GeofenceManager", "Entry: ${it.latitude}, ${it.longitude}") }


        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(context)
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("Geofence", "Google Play Services not available: $resultCode")
            return
        }

        geofencingClient.addGeofences(request, geofencePendingIntent)
            .addOnSuccessListener { Log.d(TAG, "Geofences registered") }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to register geofences", e)
                if (e is ApiException) {
                    Log.e(TAG, "Error code: ${e.statusCode}")
                    when (e.statusCode) {
                        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> Log.e(TAG, "Geofencing not available (e.g. Play Services or location off)")
                        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> Log.e(TAG, "Too many geofences registered")
                        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> Log.e(TAG, "Too many pending intents")
                    }
                }
            }

    }
}
