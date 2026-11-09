package com.barangay.microfinance.data.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class BiometricResult {
    data object Success : BiometricResult()
    data class Error(val errorCode: Int, val errString: CharSequence) : BiometricResult()
    data object Failed : BiometricResult()
    data class Unavailable(val reason: String) : BiometricResult()
}

/**
 * BiometricAuthManager: Handles hardware biometric authentication (Fingerprint & Facial Recognition).
 * Uses AndroidX BiometricPrompt for seamless and protected user access.
 *
 * NOTE FOR CROSS-PLATFORM (iOS) PARITY:
 * On iOS, the equivalent implementation uses Apple's LocalAuthentication framework:
 * ```swift
 * let context = LAContext()
 * var error: NSError?
 * if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
 *     context.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, localizedReason: "Unlock Barangay Microfinance Terminal") { success, error in
 *         // Handles Face ID / Touch ID
 *     }
 * }
 * ```
 */
class BiometricAuthManager(private val activity: FragmentActivity) {

    private val biometricManager = BiometricManager.from(activity)

    /**
     * Checks if biometric hardware is present and enrolled
     */
    fun canAuthenticate(): BiometricResult {
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricResult.Success
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricResult.Unavailable("No biometric sensor available on this device")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricResult.Unavailable("Biometric hardware currently unavailable")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricResult.Unavailable("No fingerprint or face recognition registered. Set up in Android Settings.")
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricResult.Unavailable("Security patch required")
            else -> BiometricResult.Unavailable("Biometric authentication unavailable")
        }
    }

    /**
     * Prompts the user with the native Android Biometric Dialog
     */
    suspend fun authenticate(
        title: String = "Barangay Field Collector Login",
        subtitle: String = "Scan fingerprint or face to unlock local encrypted ledger",
        description: String = "Authorized access required for loan collections & PII records"
    ): BiometricResult = suspendCancellableCoroutine { continuation ->

        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .setConfirmationRequired(false) // Smooth flow for collectors
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if (continuation.isActive) {
                        continuation.resume(BiometricResult.Success)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (continuation.isActive) {
                        continuation.resume(BiometricResult.Error(errorCode, errString))
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Android prompt remains visible for retry; callback informs view
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)

        continuation.invokeOnCancellation {
            biometricPrompt.cancelAuthentication()
        }
    }
}