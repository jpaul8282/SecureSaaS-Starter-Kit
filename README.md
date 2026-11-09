# BillingHub — Android billing UI prototype

BillingHub is a Kotlin / Jetpack Compose **showcase prototype** for exploring subscription and account-management screen designs. It is not a production billing app or payment processor. Billing state is local sample state; no backend or live Stripe integration is connected.

> **Demo only:** No real payment is taken, no subscription is created, and no verified security or compliance service is provided. Do not enter real payment-card, account, or other sensitive information.

## Current capabilities

- Android UI prototype for dashboard, illustrative plan comparison, empty billing history, and prototype/privacy information.
- Local navigation and presentation state for exploring layouts.
- A Retrofit API interface and OpenAPI contract describe a future trusted backend boundary. They are **not wired to the UI and not a deployed backend**.
- Static documentation site under `docs/`.

## Not implemented

- Authenticated backend or live Stripe PaymentSheet/payment/subscription flow.
- Webhook receiver, signature verification, or event processing.
- Real customer/invoice records, receipt downloads, or account billing.
- Backend-based export/deletion, production encryption, security audit evidence, telemetry controls, or regulatory compliance.

Checkout is disabled, does not ask for card details, and never reports a payment as successful. The repository contains no real invoices or customer records in the app's initial state. See [`backend/README.md`](backend/README.md) and [`backend/openapi.yaml`](backend/openapi.yaml) for prerequisites and API boundaries; no payments or privacy requests can be made until a trusted backend and authentication are deployed.

## Build and test

Requirements: Android Studio, JDK 17+, Android SDK 36, and Gradle compatible with the Android Gradle Plugin version in `gradle/libs.versions.toml`.

```bash
gradle :app:assembleDebug
gradle :app:testDebugUnitTest
```

A Gradle wrapper is not currently included. Use an installed compatible Gradle distribution, or add and validate the wrapper before asking end users to build the project.

## Before any Google Play release

This prototype is **not ready for production distribution as a live billing/security product**. Consult [`docs/PLAY_STORE_READINESS.md`](docs/PLAY_STORE_READINESS.md). In particular, implement and validate a backend and payment flow, accurate privacy requests and disclosures, release signing, accessibility, and device/API-level testing. Google Play approval and legal/regulatory compliance cannot be guaranteed by this project.
