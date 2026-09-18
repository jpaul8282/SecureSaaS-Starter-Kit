package com.example.model

enum class BillingCycle(val label: String, val discountPercent: Int) {
  MONTHLY("Monthly", 0),
  ANNUAL("Annual (Save 20%)", 20),
}

enum class SubscriptionTier(
  val id: String,
  val title: String,
  val monthlyPrice: Int,
  val annualPrice: Int,
  val description: String,
  val features: List<String>,
  val isPopular: Boolean = false,
) {
  STARTER(
    id = "plan_starter",
    title = "Starter",
    monthlyPrice = 0,
    annualPrice = 0,
    description = "Essential features for individuals and small test projects.",
    features = listOf(
      "Up to 3 Active Projects",
      "1 Team Seat",
      "5,000 API Requests/mo",
      "Community Forum Support",
      "Standard TLS 1.3 Encryption"
    )
  ),
  PRO(
    id = "plan_pro",
    title = "Professional",
    monthlyPrice = 29,
    annualPrice = 279,
    description = "Full-featured billing & analytics for scaling teams.",
    features = listOf(
      "Unlimited Projects",
      "10 Team Member Seats",
      "150,000 API Requests/mo",
      "Stripe Automated Invoicing",
      "Webhook SLA & Realtime Retries",
      "Priority 24/7 Email & Chat",
      "Hardware Keystore AES-256"
    ),
    isPopular = true
  ),
  ENTERPRISE(
    id = "plan_enterprise",
    title = "Enterprise",
    monthlyPrice = 99,
    annualPrice = 950,
    description = "Mission-critical security, custom SLA, and dedicated infrastructure.",
    features = listOf(
      "Custom Project Limits & Seats",
      "Unlimited High-Volume API Calls",
      "Dedicated Stripe Merchant Account",
      "SOC 2 Type II & HIPAA Compliance",
      "Custom SAML/SSO Authentication",
      "Dedicated Solutions Architect",
      "Full Cryptographic Audit Trail"
    )
  )
}

data class UserSubscription(
  val tier: SubscriptionTier,
  val billingCycle: BillingCycle,
  val status: String = "Active",
  val renewalDate: String = "Oct 18, 2026",
  val currentPeriodUsageApi: Int = 42800,
  val maxUsageApi: Int = 150000,
  val activeSeats: Int = 4,
  val maxSeats: Int = 10,
  val stripeCustomerId: String = "cus_R9x2Kl84mQpZ01",
  val stripeSubscriptionId: String = "sub_1Q5vY3KlzP923nmX81",
  val paymentMethodSummary: String = "Visa ending in 4242",
)

data class Invoice(
  val id: String,
  val invoiceNumber: String,
  val date: String,
  val amount: String,
  val status: String,
  val planName: String,
  val billingPeriod: String,
  val receiptUrl: String,
)

data class SecurityAuditEntry(
  val id: String,
  val timestamp: String,
  val eventType: String,
  val description: String,
  val severity: String, // "INFO", "SUCCESS", "WARN"
  val ipAddress: String = "192.168.1.14",
  val cryptographicProof: String,
)

data class StripeWebhookEvent(
  val eventId: String,
  val eventType: String,
  val timestamp: String,
  val status: String, // "Delivered (200 OK)", "Processing", "Pending"
  val payloadSummary: String,
)
