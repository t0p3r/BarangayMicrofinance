# Barangay Microfinance Android Application

## Organization: Barangay-Based Lending Organization
- **Platform**: Android Mobile App (Kotlin / Jetpack Compose)
- **Target Scale**: 100–500 Borrowers (Purok 1–3, Sitio Maligaya)
- **Payment Frequencies**: Daily (Arawan), Weekly (Lingguhan), Monthly (Buwanan)
- **Local Persistence**: SQLite with SQLCipher (256-bit AES DB encryption at rest)
- **Data Security**: Android Keystore AES-256-GCM hardware encryption for sensitive PII
- **Authentication**: Class 3 BiometricPrompt (Fingerprint & 3D Face Unlock) + Fallback PIN
- **Synchronization**: Offline-first WorkManager background sync engine with idempotency

## How to Open in Android Studio:
1. Open Android Studio (Hedgehog, Iguana, Jellyfish, or Ladybug).
2. Select **File > Open...** and select this unzipped folder.
3. Wait for Gradle Sync to complete.
4. Launch an Android Virtual Device (API 26+) or connect an Android physical device.
5. Click **Run 'app'** (Green play button or Shift + F10).
