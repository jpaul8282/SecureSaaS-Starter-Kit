# ProGuard & R8 Optimization and Obfuscation Rules for BillingHub
# Designed for Android SDK 36, Kotlin 2.2, Jetpack Compose, Stripe SDK, Room, and Moshi

# ============================================================================
# 1. Stacktrace De-obfuscation & Line Number Preservation (Google Play Console)
# ============================================================================
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,Exceptions
-renamesourcefileattribute SourceFile

# ============================================================================
# 2. Kotlin & Kotlinx Coroutines
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

# ============================================================================
# 3. Jetpack Compose
# ============================================================================
-keepclassmembers class * implements androidx.compose.runtime.State { *; }
-keepclassmembers class * implements androidx.compose.runtime.MutableState { *; }
-keep class androidx.compose.ui.platform.AndroidComposeView { *; }
-dontwarn androidx.compose.**

# ============================================================================
# 4. AndroidX Lifecycle & ViewModel
# ============================================================================
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# ============================================================================
# 5. Room Database & SQLite
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

# ============================================================================
# 6. Moshi JSON Serialization
# ============================================================================
-keepclasseswithmembers class * {
    @com.squareup.moshi.Json <fields>;
}

-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# ============================================================================
# 7. Retrofit & OkHttp
# ============================================================================
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ============================================================================
# 8. Stripe Android SDK
# ============================================================================
-keep class com.stripe.android.** { *; }
-keep interface com.stripe.android.** { *; }
-dontwarn com.stripe.android.**

-keepclassmembers class com.stripe.android.model.** {
    <fields>;
    <methods>;
}

# Preserve 3DS Challenge and Element bindings
-keep class com.stripe.android.stripe3ds2.** { *; }
-keep interface com.stripe.android.stripe3ds2.** { *; }
-dontwarn com.stripe.android.stripe3ds2.**

# ============================================================================
# 9. Cryptography, Android Keystore & StrongBox HSM
# ============================================================================
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }
-keep class android.security.keystore.** { *; }
-dontwarn android.security.keystore.**

# Preserve biometric authentication wrappers
-keep class androidx.biometric.** { *; }
-dontwarn androidx.biometric.**

# ============================================================================
# 10. BillingHub Domain Models & Data Structures
# ============================================================================
-keep class com.example.data.models.** { *; }
-keep class com.example.data.local.** { *; }
-keep class com.example.domain.** { *; }
-keep class com.example.BuildConfig { *; }
