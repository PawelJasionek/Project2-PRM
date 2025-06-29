import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val REQUEST_FINE_LOCATION = 1002
private const val REQUEST_BACKGROUND_LOCATION = 1003

fun checkAndRequestLocationPermission(activity: Activity): Boolean {
    val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION

    val hasFine = ContextCompat.checkSelfPermission(activity, fineLocation) == PackageManager.PERMISSION_GRANTED

    val hasBackground = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    if (!hasFine) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(fineLocation),
            REQUEST_FINE_LOCATION
        )
        return false
    }

    if (!hasBackground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            REQUEST_BACKGROUND_LOCATION
        )
        return false
    }

    return true
}
