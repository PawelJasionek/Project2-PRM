package com.example.digitaldiary.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

object LocationUtils {

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(context: Context): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location -> cont.resume(location) }
                .addOnFailureListener { cont.resume(null) }
        }
    }

    fun getCityName(context: Context, location: Location?): String {
        return try {
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                address?.firstOrNull()?.locality ?: "Location unknown"
            } else {
                "Location unknown"
            }
        } catch (e: Exception) {
            "Location unknown"
        }
    }
}
