package com.barangay.microfinance.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.barangay.microfinance.data.security.BiometricAuthManager
import com.barangay.microfinance.data.security.BiometricResult
import com.barangay.microfinance.ui.screens.MainNavigationHub
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private lateinit var biometricAuthManager: BiometricAuthManager
    private var isUserAuthenticated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricAuthManager = BiometricAuthManager(this)

        // Trigger biometric login on app launch
        promptBiometricLogin()

        setContent {
            MainNavigationHub()
        }
    }

    private fun promptBiometricLogin() {
        lifecycleScope.launch {
            when (val result = biometricAuthManager.authenticate()) {
                is BiometricResult.Success -> {
                    isUserAuthenticated = true
                    Toast.makeText(this@MainActivity, "Biometrics Verified. Decrypting SQLite Ledger...", Toast.LENGTH_SHORT).show()
                }
                is BiometricResult.Error -> {
                    Toast.makeText(this@MainActivity, "Auth Error: ${result.errString}", Toast.LENGTH_LONG).show()
                }
                is BiometricResult.Unavailable -> {
                    Toast.makeText(this@MainActivity, "Biometrics: ${result.reason}. Using PIN.", Toast.LENGTH_LONG).show()
                }
                BiometricResult.Failed -> {
                    Toast.makeText(this@MainActivity, "Fingerprint/Face not recognized. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}