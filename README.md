# BillingHub — Android billing UI prototype

BillingHub is a Kotlin / Jetpack Compose **showcase prototype** for exploring subscription and account-management screen designs. It is not a production billing app or a payment processor. Billing state, invoices, webhook events, and security/audit entries in the current app are sample data or local simulations.

> **Important:** No real payment is taken, no real Stripe subscription is created, and the app does not provide verified security controls or regulatory compliance. Do not enter real payment-card, account, or other sensitive information. Do not use screenshots or copy that implies otherwise.

## Current capabilities

- Android UI prototype for dashboard, plan selection, billing history, security/privacy, and a user guide.
- Local demo flows for exploring UI states.
- Static documentation site under `docs/`.

## Not implemented

- A backend that authenticates users or creates Stripe PaymentIntents/Subscriptions.
- Stripe Android PaymentSheet or a real payment flow.
- A real webhook receiver with signature verification and idempotent event processing.
- Persisted user billing data, actual data export/deletion, or production audit records.
- The encryption, telemetry, biometric, and compliance guarantees previously described in parts of the UI/docs.

The checkout screen intentionally does not collect card details or report a simulated payment as successful. The prototype must not be used to process payments.

## Build and test

Requirements: Android Studio, JDK 17+, Android SDK 36, and Gradle compatible with the Android Gradle Plugin version in `gradle/libs.versions.toml`.

```bash
gradle :app:assembleDebug
gradle :app:testDebugUnitTest
```

A Gradle wrapper is not currently included. Use an installed compatible Gradle distribution, or add and validate the wrapper before asking end users to build the project.

## Before any Google Play release

This prototype is **not ready for Google Play production distribution**. At a minimum:

1. Implement and independently test a backend and Stripe integration; never put Stripe secret or webhook signing keys in the Android app.
2. Replace all sample content and simulated flows with real, authenticated data—or keep the app clearly demo-only and do not represent simulations as actual services.
3. Implement and verify account deletion and export, then ensure the Play Data safety form, privacy policy, and in-app disclosures match the released binary and actual data flows.
4. Provide a real privacy policy, support contact, app identity, release signing, accessibility review, and device/API-level testing.
5. Remove private release mapping artifacts from the public repository and archive them securely per release.

See [`docs/PLAY_STORE_READINESS.md`](docs/PLAY_STORE_READINESS.md) for the release checklist. Google Play approval and legal/regulatory compliance cannot be guaranteed by this project.
