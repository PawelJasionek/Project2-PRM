package com.example.digitaldiary.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.*
import com.example.digitaldiary.R

@Composable
fun LockScreen(onAuthenticated: () -> Unit) {
    val context = LocalContext.current

    var pinInput by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }
    var isFirstTime by remember { mutableStateOf(false) }


    val sharedPrefs by remember {
        mutableStateOf(
            EncryptedSharedPreferences.create(
                context,
                "secure_prefs",
                MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        )
    }

    LaunchedEffect(Unit) {
        isFirstTime = sharedPrefs.getString("user_pin", null).isNullOrEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isFirstTime) stringResource(R.string.pin_title_new) else stringResource(R.string.pin_title_enter) )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pinInput,
            onValueChange = { pinInput = it },
            label = { Text(stringResource(R.string.pin_label) ) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword)
        )

        if (isFirstTime) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = pinConfirm,
                onValueChange = { pinConfirm = it },
                label = { Text(stringResource(R.string.pin_confirm_label) ) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        val toastTextMisMatch = stringResource(R.string.pin_mismatch)
        val toastTextPinTooShort = stringResource(R.string.pin_too_short)
        val toastTextPinSaved = stringResource(R.string.pin_saved)
        val toastTextPinIncorrect = stringResource(R.string.pin_incorrect)
        Button(onClick = {
            if (isFirstTime) {
                when {
                    pinInput.length < 4 -> {
                        Toast.makeText(context, toastTextPinTooShort , Toast.LENGTH_SHORT).show()
                    }
                    pinInput != pinConfirm -> {

                        Toast.makeText(context, toastTextMisMatch , Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        sharedPrefs.edit().putString("user_pin", pinInput).apply()
                        Toast.makeText(context, toastTextPinSaved , Toast.LENGTH_SHORT).show()
                        onAuthenticated()
                    }
                }
            } else {
                val stored = sharedPrefs.getString("user_pin", null)
                if (pinInput == stored) {
                    onAuthenticated()
                } else {
                    Toast.makeText(context, toastTextPinIncorrect , Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(if (isFirstTime) stringResource(R.string.pin_save_button)  else stringResource(R.string.pin_unlock_button) )
        }
    }
}
