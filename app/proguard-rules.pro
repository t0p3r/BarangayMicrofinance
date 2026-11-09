# SQLCipher keep rules
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Room SQLite keep rules
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**

# AndroidX Security Crypto & Keystore
-keep class androidx.security.crypto.** { *; }

# BiometricPrompt
-keep class androidx.biometric.** { *; }

# WorkManager
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }

# Gson serialization for SyncQueue payloads
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}