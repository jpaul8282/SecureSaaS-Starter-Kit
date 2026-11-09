# Backend integration contract (not a deployed backend)

`openapi.yaml` describes the security boundary and API contract for a future trusted service. The Android app currently does not call these endpoints, and no server implementation, authentication provider, Stripe account, database, or deployment is included. This contract does not enable real payments or privacy requests by itself.

## Required server responsibilities

- Authenticate every customer request using a verified identity token; map that identity to a customer server-side. Enforce ownership for every invoice and privacy request.
- Keep Stripe secret and webhook signing keys in a server-side secret manager. The Android app may receive only a Stripe publishable key and short-lived SDK client secrets over TLS.
- Resolve plan identifiers to server-controlled Stripe Price IDs. Never trust price, amount, customer ID, or subscription status from the app.
- Create subscriptions with idempotency, appropriate payment behavior, and Stripe PaymentSheet customer configuration. Treat client confirmation as UI only; authoritative subscription entitlement changes come from verified webhooks.
- Verify webhook signatures against the raw body with Stripe's maintained server SDK; persist event IDs transactionally before effects and safely handle retries/out-of-order events.
- Implement privacy export/deletion as durable asynchronous requests. Require recent authentication for deletion, report accepted/pending/completed/failed honestly, and define provider cancellation, lawful retention, and audit policies.
- Rate-limit endpoints, validate request schemas, use TLS, redact credentials and personal/payment data from logs, monitor failures, and run security review and integration tests.

## Android client contract

`app/src/main/java/com/example/service/backend/BillingBackendApi.kt` provides Retrofit request/response declarations only. Integrating it requires an approved API base URL, a real user-authentication/token provider, network/error handling, and end-to-end testing. It is intentionally not wired to the demo UI. Do not add a fake bearer token or place provider secrets in the mobile app.

## Enablement checklist

1. Select and configure an identity provider and deployed backend.
2. Implement all operations in the OpenAPI contract, with database migrations and ownership checks.
3. Set up Stripe test-mode products/prices and webhook signing secret in the backend secret manager.
4. Add and configure Stripe's official Android SDK PaymentSheet with client secrets returned by the authenticated server; no raw card fields in app code.
5. Test succeeded, declined, 3DS/action-required, canceled, duplicate/out-of-order webhook, network retry, export, deletion, and unauthorized access scenarios.
6. Complete privacy and Google Play disclosure review against observed data collection/sharing for the released artifact.

Until these steps are complete, the app must remain demo-only and checkout/privacy operations must remain disabled.
