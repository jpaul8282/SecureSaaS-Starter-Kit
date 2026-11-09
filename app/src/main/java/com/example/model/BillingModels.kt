package com.example.model

enum class BillingCycle(val label: String, val discountPercent: Int) {
  MONTHLY("Monthly", 0),
  ANNUAL("Annual (illustrative)", 0),
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
    id = "demo_starter",
    title = "Starter",
    monthlyPrice = 0,
    annualPrice = 0,
    description = "Illustrative entry-level plan for the UI prototype.",
    features = listOf("Example project limits", "Example usage dashboard", "No live service included"),
  ),
  PRO(
    id = "demo_professional",
    title = "Professional",
    monthlyPrice = 29,
    annualPrice = 279,
    description = "Illustrative plan details; prices and features are not purchasable.",
    features = listOf("Example team controls", "Example usage dashboard", "No billing or support SLA included"),
    isPopular = true,
  ),
  ENTERPRISE(
    id = "demo_enterprise",
    title = "Enterprise",
    monthlyPrice = 99,
    annualPrice = 950,
    description = "Illustrative plan only; no enterprise service is provided.",
    features = listOf("Example plan comparison", "No compliance certification included", "No dedicated infrastructure included"),
  ),
}

data class UserSubscription(
  val tier: SubscriptionTier,
  val billingCycle: BillingCycle,
  val status: String = "Demo",
  val renewalDate: String = "Not connected",
  val currentPeriodUsageApi: Int = 0,
  val maxUsageApi: Int = 0,
  val activeSeats: Int = 0,
  val maxSeats: Int = 1,
  val stripeCustomerId: String = "",
  val stripeSubscriptionId: String = "",
  val paymentMethodSummary: String = "No payment method",
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
  val subtotal: String = amount,
  val tax: String = "$0.00",
  val currency: String = "USD",
  val paymentMethod: String = "Not available",
  val paidAt: String = date,
  val items: List<InvoiceLineItem> = emptyList(),
  val pdfFileName: String = "Receipt_${invoiceNumber}.pdf",
) {
  val numericAmount: Double
    get() = amount.replace("$", "").toDoubleOrNull() ?: 0.0

  val isPaid: Boolean
    get() = status.equals("Paid", ignoreCase = true)
}

data class InvoiceLineItem(
  val description: String,
  val quantity: Int = 1,
  val unitAmount: String,
  val totalAmount: String,
)
