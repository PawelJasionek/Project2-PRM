import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
fun logCurrentLocation(context: Context) {
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("LocationCheck", "Current location: ${location.latitude}, ${location.longitude}")
            } else {
                Log.w("LocationCheck", "Location is null")
            }
        }
        .addOnFailureListener { e ->
            Log.e("LocationCheck", "Failed to get location", e)
        }
}
