# ProGuard & R8 Optimization and Obfuscation Rules for BillingHub
# Designed for Android SDK 36, Kotlin 2.2, Jetpack Compose, Firebase, Room, Moshi, Retrofit
# Target: Secure SaaS application with proper stack trace deobfuscation for Google Play Console

# ============================================================================
# 1. Stacktrace De-obfuscation & Line Number Preservation (Google Play Console)
# ============================================================================
# Generate mapping file for Play Console upload and deobfuscation
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions
-renamesourcefileattribute SourceFile

# ============================================================================
# 2. Android Entry Points (Activities, Services, Receivers, Providers)
# ============================================================================
-keep public class * extends android.app.Activity {
    <init>(...);
}

-keep public class * extends android.app.Service {
    <init>(...);
}

-keep public class * extends android.content.BroadcastReceiver {
    <init>(...);
}

-keep public class * extends android.content.ContentProvider {
    <init>(...);
}

-keep public class * extends android.app.Application {
    <init>(...);
}

-keep public class * extends android.content.Context {
    *** get*(...);
}

# ============================================================================
# 3. Kotlin & Kotlinx Coroutines
# ============================================================================
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keepclassmembers class **$WhenMappings {
    <fields>;
}

-dontwarn kotlin.reflect.**
-keep class kotlin.reflect.** { *; }

-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Kotlin serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}

# ============================================================================
# 4. Jetpack Compose
# ============================================================================
-keepclassmembers class * implements androidx.compose.runtime.State { *; }
-keepclassmembers class * implements androidx.compose.runtime.MutableState { *; }
-keep class androidx.compose.ui.platform.AndroidComposeView { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.material.** { *; }
-dontwarn androidx.compose.**

# Compose runtime preservation
-keep class androidx.compose.runtime.Composer { *; }
-keepclassmembers class androidx.compose.runtime.Composer {
    public <init>(...);
}

# ============================================================================
# 5. AndroidX Lifecycle & ViewModel
# ============================================================================
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

-keep class androidx.lifecycle.** { *; }
-keepclassmembers class androidx.lifecycle.** {
    <init>(...);
}

# ============================================================================
# 6. Room Database & SQLite
# ============================================================================
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

-keep @androidx.room.Entity class * {
    <fields>;
}

-keep @androidx.room.Dao interface * {
    <methods>;
}

-keepclassmembers class * {
    @androidx.room.Insert <methods>;
    @androidx.room.Update <methods>;
    @androidx.room.Delete <methods>;
    @androidx.room.Query <methods>;
    @androidx.room.RawQuery <methods>;
}

# Keep generated DAO implementations
-keep class **.Impl_* { *; }
-keep class **.Impl_*$Callback { *; }

# ============================================================================
# 7. Moshi JSON Serialization
# ============================================================================
-keepclasseswithmembers class * {
    @com.squareup.moshi.Json <fields>;
}

-keep @com.squareup.moshi.JsonClass class * { 
    <fields>; 
    <methods>; 
}

-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# Keep Moshi adapters
-keep class **.MoshiAdapterFactory { *; }
-keep class **.MoshiAdapter { *; }

# ============================================================================
# 8. Retrofit & OkHttp
# ============================================================================
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Preserve Retrofit service methods
-keepclassmembers interface * {
    @retrofit2.http.GET <methods>;
    @retrofit2.http.POST <methods>;
    @retrofit2.http.PUT <methods>;
    @retrofit2.http.PATCH <methods>;
    @retrofit2.http.DELETE <methods>;
    @retrofit2.http.HEAD <methods>;
    @retrofit2.http.OPTIONS <methods>;
}

# ============================================================================
# 9. Firebase & Google Play Services
# ============================================================================
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.android.gms.internal.recaptchabase.**
-dontwarn com.google.android.gms.measurement.**
-dontwarn com.google.firebase.appcheck.**

# Keep Firebase App Check and Auth classes needed at runtime
-keep class com.google.firebase.appcheck.** { *; }
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.common.api.** { *; }

# ============================================================================
# 10. Google Gemini AI (GenAI)
# ============================================================================
-keep class com.google.ai.client.generativeai.** { *; }
-keep class com.google.ai.generativelanguage.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# ============================================================================
# 11. Cryptography, Android Keystore & Hardware-backed Security
# ============================================================================
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }
-keep class android.security.keystore.** { *; }
-dontwarn android.security.keystore.**

# Preserve cryptographic provider implementations
-keepclassmembers class java.security.KeyStore {
    *** getInstance(...);
}

-keepclassmembers class javax.crypto.Cipher {
    *** getInstance(...);
}

# ============================================================================
# 12. Biometric Authentication (Fingerprint, Face, Iris)
# ============================================================================
-keep class androidx.biometric.** { *; }
-keep class androidx.biometric.BiometricPrompt$* { *; }
-dontwarn androidx.biometric.**

-keep class android.hardware.biometrics.** { *; }
-dontwarn android.hardware.biometrics.**

# ============================================================================
# 13. AndroidX Credentials (Credential Manager API)
# ============================================================================
-keep class androidx.credentials.** { *; }
-dontwarn androidx.credentials.**

# ============================================================================
# 14. BillingHub Application-Specific Classes & Stripe SDK
# ============================================================================
# Stripe Android SDK
-dontwarn com.stripe.android.**
-keep class com.stripe.android.** { *; }

# Keep all app domain models and logic
-keep class com.example.** { 
    <init>(...); 
    *** *(...); 
}

-keep class com.example.model.** { *; }
-keep class com.example.service.StripeBillingManager { *; }

-keep class com.aistudio.billinghub.** { 
    <init>(...); 
    *** *(...); 
}

# Keep application entry point
-keep class com.example.MainActivity { *; }
-keep class com.example.di.** { *; }
-keep class com.example.ui.** { *; }
-keep class com.example.domain.** { *; }
-keep class com.example.data.** { *; }

# Keep BuildConfig
-keep class com.example.BuildConfig { *; }

# ============================================================================
# 15. Serialization & Data Models
# ============================================================================
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================================================
# 16. Enumeration Classes
# ============================================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================================================
# 17. Native Methods (JNI)
# ============================================================================
-keepclasseswithmembernames class * {
    native <methods>;
}

# ============================================================================
# 18. Logging Removal (Release Builds Only)
# ============================================================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ============================================================================
# 19. View Constructors (XML Inflation)
# ============================================================================
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
}

# ============================================================================
# 20. Resources & Manifest
# ============================================================================
-keep class **.R$* {
    public static <fields>;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# ============================================================================
# 21. Optimization Settings
# ============================================================================
-dontusemixedcaseclassnames
-verbose

# ============================================================================
# 22. Warnings Handling & Play Store Deobfuscation
# ============================================================================
-ignorewarnings
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
-dontwarn sun.misc.**
-dontwarn com.sun.**
-dontwarn javax.naming.**
-dontwarn android.nfc.**
