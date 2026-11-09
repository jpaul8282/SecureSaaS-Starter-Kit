package com.example

import com.example.model.BillingCycle
import com.example.model.SubscriptionTier
import com.example.service.StripeBillingManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun billingManagerStartsWithNoCustomerOrBillingRecords() {
    val manager = StripeBillingManager()
    assertEquals(SubscriptionTier.STARTER, manager.subscription.value.tier)
    assertEquals("Demo", manager.subscription.value.status)
    assertTrue(manager.subscription.value.stripeCustomerId.isBlank())
    assertTrue(manager.subscription.value.stripeSubscriptionId.isBlank())
    assertEquals("No payment method", manager.subscription.value.paymentMethodSummary)
    assertTrue(manager.invoices.value.isEmpty())
  }

  @Test
  fun checkoutDoesNotAcceptCardDataOrChangeSubscription() = runBlocking {
    val manager = StripeBillingManager()
    val original = manager.subscription.value

    val result = manager.processSubscriptionChange(SubscriptionTier.PRO, BillingCycle.MONTHLY)

    assertTrue(result.isFailure)
    assertEquals(original, manager.subscription.value)
    assertTrue(manager.invoices.value.isEmpty())
  }

  @Test
  fun invoiceFetchFailsClearlyWhenNoBackendIsConfigured() = runBlocking {
    val manager = StripeBillingManager()
    val result = manager.fetchLast12MonthsInvoices()

    assertTrue(result.isFailure)
    assertFalse(result.isSuccess)
    assertTrue(manager.invoices.value.isEmpty())
  }

  @Test
  fun annualPlanLabelDoesNotClaimDiscount() {
    assertEquals(0, BillingCycle.ANNUAL.discountPercent)
    assertTrue(BillingCycle.ANNUAL.label.contains("illustrative"))
  }
}
