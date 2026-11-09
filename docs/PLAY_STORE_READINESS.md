# Google Play and public-user readiness

## Current status: showcase prototype

The app demonstrates Android layouts and local sample states only. It does not process payments, connect to Stripe, receive or verify webhooks, export or delete real account data, or provide production security controls. Do not enter real card details or sensitive personal data.

The Play Store Data safety form must describe the final released binary and all SDK behavior, not the old claims in archived screenshots or copy. This repository is not currently ready to be represented as a live billing/security product in a Play Store listing.

## Required before production use or store release

- Implement a trusted authenticated backend that creates Stripe customer/subscription/payment objects. Keep all Stripe secret and webhook signing keys server-side.
- Integrate the official Stripe Android SDK PaymentSheet. Never collect, pass, log, or persist raw card number, expiry, or CVC in app business logic.
- Receive and verify webhooks on the backend, with timestamp tolerance, durable idempotency, retries, and tested subscription state transitions.
- Fetch invoices from authenticated APIs. Never generate local records or receipts that look like completed real transactions.
- Implement account export and deletion with accurate states for backend and provider operations; do not claim a request succeeded until confirmed.
- Review privacy policy, support contact, accessibility, localization, account deletion discoverability, retention, and Play Console Data safety disclosures against actual shipped behavior.
- Test payment failure, cancellation, authentication challenges, network loss, duplicate webhooks, export/deletion, and supported Android versions.
- Configure protected release signing; do not use a debug key for release artifacts. Keep R8 mapping files private and out of public releases.
- Configure maintained, pinned CI workflows to build and test the actual app.

These are engineering recommendations, not legal advice or a guarantee of Google Play approval or regulatory compliance.
