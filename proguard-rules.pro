# ProGuard configuration for SecureSaaS-Starter-Kit
# This file specifies the code obfuscation rules for release builds

# General rules for Android/Kotlin
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, EnclosingMethod

# Keep all custom application classes
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Fragment
-keep public class * extends android.os.Binder
-keep public class * extends java.lang.Throwable

# Keep View constructors for inflation from XML
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Keep all kotlin classes that are annotated
-keep @interface * 
-keep @kotlin.Metadata class *
-keep class **$*$*$* { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable implementations
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep library-specific rules
# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# AndroidX
-keep class androidx.** { *; }
-keep public class androidx.** { public *; }

# Security-related libraries
-keep class javax.security.** { *; }
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# Firebase (if used)
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep all SaaS-specific classes
-keep class com.securesaas.** { *; }
-keepclassmembers class com.securesaas.** {
    *** *(...);
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep R classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Optimization rules
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Don't obfuscate
-dontobfuscate

# Verbose output
-printmapping mapping.txt
-printseeds seeds.txt
-printusage unused.txt
