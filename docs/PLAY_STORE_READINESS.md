# Google Play and public-user readiness

## Current status: prototype only

The app currently presents a billing/security product concept, but the application logic contains local, simulated billing and sample data. The payment UI is intentionally non-functional and must not collect payment credentials or imply that payment succeeded. The app is not ready to be represented as a live billing or security product in a Play Store listing.

Do not submit a Data safety declaration based on the old README/UI claims. First inventory what the actual released binary and all SDKs collect, process, and share; then make the Play Console disclosure and privacy policy match those facts.

## Required before production payments

- Build a trusted backend that authenticates the user, creates Stripe customer/subscription/payment objects, and returns only the client secrets needed by the app. Never embed Stripe secret keys or webhook signing secrets in an Android build.
- Integrate the official Stripe Android SDK/PaymentSheet. Card details must be entered only through the provider's secure UI and never passed into app business logic, logged, or persisted.
- Implement webhook signature verification, timestamp tolerance, durable event-id idempotency, retries, and subscription state updates on the backend. The mobile app is not a webhook endpoint.
- Fetch invoices/subscriptions from authenticated backend APIs; do not generate plausible-looking IDs, receipts, successful payments, or audit evidence locally.
- Implement account export and deletion with clear success/failure states and backend/provider semantics. A local UI confirmation is not proof of erasure.
- Review privacy policy, retention, support contact, accessibility, localization, account deletion discoverability, and Play Console Data safety disclosures against the final binary.
- Test payment failure, cancellation, retry, authentication challenges, network loss, duplicate webhooks, account deletion, data export, and accessibility on supported devices.

## Release and repository hygiene

- Configure a dedicated release signing key through protected CI secrets. Never sign a release with a debug key.
- Keep user data out of Android backups unless there is a reviewed, documented need and secure restore design.
- Do not publish R8 mapping files or other sensitive release diagnostics in this public repository; upload them privately to the appropriate release tooling.
- Use pinned, maintained GitHub Actions and a CI workflow that builds and tests the actual application.

These are engineering recommendations, not legal advice or a guarantee of Google Play approval or compliance.
