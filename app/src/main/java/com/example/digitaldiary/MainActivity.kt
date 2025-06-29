package com.example.digitaldiary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.digitaldiary.ui.LockScreen

import com.example.digitaldiary.viewmodel.checkAndRequestNotificationPermission

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var isAuthenticated by remember { mutableStateOf(false)}
            if(isAuthenticated) {
                DigitalDiaryApp()
            }else{
                LockScreen(onAuthenticated = {isAuthenticated = true})
            }
        }

        if (!checkAndRequestNotificationPermission(this)) {
            Log.d("Permissions", "Notification permission not yet granted.")
        }else Log.d("Permissions", "Notification permission granted.")

    }

}