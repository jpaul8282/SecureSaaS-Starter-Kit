# BillingHub Play Console Deobfuscation Setup Guide

This guide explains how to set up and upload deobfuscation files to Google Play Console for proper crash reporting and stack trace analysis.

## Overview

When you release a Kotlin/Android app with code obfuscation (via R8/ProGuard), the stack traces in the Play Console crash logs are obfuscated. Without deobfuscation files, you can't read the actual error locations.

**BillingHub Configuration:**
- **Package Name:** `com.aistudio.billinghub.vxfkrz`
- **App Namespace:** `com.example`
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36 (Android 15)
- **Version Code:** 6
- **Build Tool:** Gradle with R8 (Android Gradle Plugin 8.x+)

---

## Step 1: Generate Mapping File During Release Build

The `app/proguard-rules.pro` is configured to automatically generate mapping files during release builds.

### Build Release APK/AAB:

```bash
# Build release APK
./gradlew app:assembleRelease

# OR build release App Bundle (preferred for Play Store)
./gradlew app:bundleRelease
```

### Expected Output Files:

After a successful release build, the following files are generated:

```
app/build/outputs/mapping/release/
├── mapping.txt          ← **UPLOAD THIS TO PLAY CONSOLE**
├── seeds.txt            ← Entry points kept by R8
└── usage.txt            ← Unused code removed by R8
```

**Key File:** `mapping.txt` — This is the deobfuscation file.

---

## Step 2: Upload to Google Play Console

### Location in Play Console:

1. **Log in** to [Google Play Console](https://play.google.com/console)
2. **Select** your app: **BillingHub** (`com.aistudio.billinghub.vxfkrz`)
3. Navigate to: **Release** → **Setup** → **App integrity** (or **Internal testing**)
4. Scroll down to **Deobfuscation files**
5. Click **Upload deobfuscation file**
6. Select the **`mapping.txt`** file from:
   ```
   app/build/outputs/mapping/release/mapping.txt
   ```

### Important Notes:

- **One mapping file per release version**: Each App version needs its corresponding mapping file
- **Upload before or after release**: You can upload anytime; crashes will be deobfuscated retroactively
- **Versioning**: The mapping file must match the `versionCode` and `versionName` in your release build
- **Sensitive data**: Keep `mapping.txt` secure (don't commit to public repos)

---

## Step 3: Verify Deobfuscation in Crash Reports

Once uploaded, you can verify deobfuscation:

1. Go to **Android Vitals** → **Crashes & ANRs**
2. Select any crash from your released version
3. The stack trace should show **actual class names and method names** instead of obfuscated names like `a.b.c`

### Example:

**Before Deobfuscation (without mapping):**
```
Exception in thread "main"
  at a.b.c.d(Unknown Source:42)
  at e.f.g.h(Unknown Source:15)
```

**After Deobfuscation (with mapping file):**
```
Exception in thread "main"
  at com.example.data.repository.UserRepository.fetchUser(UserRepository.kt:42)
  at com.example.ui.screens.LoginScreen.authenticate(LoginScreen.kt:15)
```

---

## Step 4: Store Mapping Files for Version History

Create a secure archive of all mapping files by version:

```bash
mkdir -p mappings-archive

# After each release build:
cp app/build/outputs/mapping/release/mapping.txt \
   mappings-archive/mapping-v6-release.txt

# Reference by version code and name
# Example: mapping-vCode-vName-flavor.txt
# Example: mapping-v6-6.0-release.txt
```

### Directory Structure:

```
mappings-archive/
├── mapping-v1-1.0-release.txt
├── mapping-v2-2.0-release.txt
├── mapping-v3-3.0-release.txt
├── mapping-v4-4.0-release.txt
├── mapping-v5-5.0-release.txt
├── mapping-v6-6.0-release.txt   ← Current release
```

---

## Step 5: Automate Upload (Optional CI/CD)

For CI/CD pipelines (GitHub Actions, GitLab CI, etc.), automate mapping upload:

### Example GitHub Actions Workflow:

```yaml
name: Upload Deobfuscation to Play Console

on:
  workflow_dispatch:
    inputs:
      version_code:
        description: 'Version Code (e.g., 6)'
        required: true
      mapping_file:
        description: 'Path to mapping.txt file'
        required: true

jobs:
  upload-deobfuscation:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '11'

      - name: Build Release APK
        run: ./gradlew app:assembleRelease

      - name: Upload to Play Console
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_CONSOLE_SERVICE_ACCOUNT }}
          packageName: com.aistudio.billinghub.vxfkrz
          releaseFiles: 'app/build/outputs/apk/release/*.apk'
          mappingFile: 'app/build/outputs/mapping/release/mapping.txt'
          track: 'internal'
          status: 'draft'
```

---

## Step 6: ProGuard Configuration Best Practices

The `app/proguard-rules.pro` file is already optimized for BillingHub:

- ✅ **Security classes kept:** Cryptography, Keystore, Biometric
- ✅ **Firebase kept:** All Firebase SDKs and services
- ✅ **Serialization kept:** Room, Moshi, Retrofit, Parcelable
- ✅ **App models kept:** All `com.example.*` and `com.aistudio.*` classes
- ✅ **Logging removed:** Debug and verbose logs removed in release builds
- ✅ **Line numbers preserved:** Stack traces remain readable after deobfuscation

### Key Rules:

```proguard
# De-obfuscation for Play Console
-keepattributes SourceFile,LineNumberTable
-printmapping build/outputs/mapping/release/mapping.txt

# Keep app domain
-keep class com.example.** { <init>(...); *** *(...); }
-keep class com.aistudio.billinghub.** { <init>(...); *** *(...); }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
```

---

## Troubleshooting

### Q: Crashes still show obfuscated names?
**A:** Ensure the uploaded `mapping.txt` matches the exact version code of the released build.

### Q: Where is `mapping.txt` generated?
**A:** It's only generated during **release** builds. Check:
```
app/build/outputs/mapping/release/mapping.txt
```

### Q: Can I reuse the same mapping file?
**A:** No. Each version code must have its own mapping file.

### Q: Can I upload multiple mapping files at once?
**A:** No. Upload one per version. Play Console will automatically match them.

### Q: How do I know if deobfuscation worked?
**A:** Check **Android Vitals** → **Crashes**. If stack traces show actual class names, it worked.

---

## Security Considerations

⚠️ **Important:** The `mapping.txt` file reveals:
- All class names and method names (even obfuscated ones)
- Package structure
- Method signatures

### Best Practices:

1. ✅ **Do NOT commit** `mapping.txt` to public Git repositories
2. ✅ **Store securely** (e.g., GitHub Secrets, encrypted vault)
3. ✅ **Only share** with your team on a need-to-know basis
4. ✅ **Archive versions** in a private secure location
5. ✅ **Use `.gitignore`** to exclude build output:
   ```
   # .gitignore
   app/build/outputs/mapping/
   ```

---

## Additional Resources

- [Google Play Console Docs: Deobfuscation](https://support.google.com/googleplay/android-developer/answer/7985351)
- [Android Gradle Plugin R8 Configuration](https://developer.android.com/build/shrink-code)
- [ProGuard & R8 Rules Reference](https://www.guardsquare.com/proguard)
- [Firebase Crash Reporting](https://firebase.google.com/docs/crashlytics)

---

## BillingHub Release Checklist

Before releasing version to Play Console:

- [ ] Build release APK/AAB: `./gradlew app:bundleRelease`
- [ ] Verify `app/build/outputs/mapping/release/mapping.txt` exists
- [ ] Archive mapping file: `cp mapping.txt mappings-archive/mapping-v6-6.0-release.txt`
- [ ] Upload mapping to Play Console (Deobfuscation files section)
- [ ] Test crash reporting in Android Vitals
- [ ] Verify stack traces are readable after 24 hours
- [ ] Document release version in project wiki

---

**Last Updated:** October 3, 2026  
**App:** BillingHub (com.aistudio.billinghub.vxfkrz)  
**Version:** 6.0 (Code 6)  
**SDK Target:** Android 15 (API 36)
