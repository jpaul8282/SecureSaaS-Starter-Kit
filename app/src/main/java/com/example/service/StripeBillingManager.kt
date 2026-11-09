package com.example.service

import android.content.Context
import com.example.model.BillingCycle
import com.example.model.Invoice
import com.example.model.SubscriptionTier
import com.example.model.UserSubscription
import com.example.util.PdfReceiptGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Local showcase state only. No account, payment provider, invoice API, webhook endpoint,
 * privacy service, or production security implementation is connected to this class.
 */
class StripeBillingManager {
  private val _subscription = MutableStateFlow(
    UserSubscription(
      tier = SubscriptionTier.STARTER,
      billingCycle = BillingCycle.MONTHLY,
      status = "Demo",
      renewalDate = "Not connected",
      currentPeriodUsageApi = 0,
      maxUsageApi = 0,
      activeSeats = 0,
      maxSeats = 1,
      stripeCustomerId = "",
      stripeSubscriptionId = "",
      paymentMethodSummary = "No payment method",
    ),
  )
  val subscription: StateFlow<UserSubscription> = _subscription.asStateFlow()

  private val _billingCycle = MutableStateFlow(BillingCycle.MONTHLY)
  val billingCycle: StateFlow<BillingCycle> = _billingCycle.asStateFlow()

  // There are deliberately no seeded customer IDs, invoices, security records, or webhook events.
  private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
  val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

  private val _isFetchingInvoices = MutableStateFlow(false)
  val isFetchingInvoices: StateFlow<Boolean> = _isFetchingInvoices.asStateFlow()

  private val _lastFetchedTimestamp = MutableStateFlow("Demo only — billing is not connected")
  val lastFetchedTimestamp: StateFlow<String> = _lastFetchedTimestamp.asStateFlow()

  fun setBillingCycle(cycle: BillingCycle) {
    _billingCycle.value = cycle
  }

  /** A safe placeholder: never accepts raw card data or claims a purchase succeeded. */
  suspend fun processSubscriptionChange(
    @Suppress("UNUSED_PARAMETER") newTier: SubscriptionTier,
    @Suppress("UNUSED_PARAMETER") cycle: BillingCycle,
  ): Result<String> = Result.failure(
    UnsupportedOperationException("Checkout is disabled: this showcase has no payment backend."),
  )

  /** No direct Stripe API call is made from the app. */
  suspend fun fetchLast12MonthsInvoices(
    @Suppress("UNUSED_PARAMETER") forceRefresh: Boolean = false,
  ): Result<List<Invoice>> = Result.failure(
    UnsupportedOperationException("Invoices are unavailable: this showcase has no billing backend."),
  )

  /** Never create a plausible-looking receipt for a sample or unverified invoice. */
  fun downloadPdfReceipt(
    @Suppress("UNUSED_PARAMETER") context: Context,
    @Suppress("UNUSED_PARAMETER") invoice: Invoice,
  ): Result<File> = Result.failure(
    UnsupportedOperationException("Receipt downloads are disabled in this showcase."),
  )
}
