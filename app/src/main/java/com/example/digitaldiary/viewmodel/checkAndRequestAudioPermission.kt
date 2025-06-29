import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val REQUEST_RECORD_AUDIO_PERMISSION = 1001

fun checkAndRequestAudioPermission(activity: Activity): Boolean {
    val permission = Manifest.permission.RECORD_AUDIO

    return if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_RECORD_AUDIO_PERMISSION)
        false
    } else {
        true
    }
}