package com.example

import com.example.model.BillingCycle
import com.example.model.SubscriptionTier
import com.example.service.StripeBillingManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun stripeBillingManager_initialStateIsCorrect() {
    val manager = StripeBillingManager()
    assertEquals(SubscriptionTier.PRO, manager.subscription.value.tier)
    assertEquals(BillingCycle.MONTHLY, manager.billingCycle.value)
    assertTrue(manager.invoices.value.isNotEmpty())
    assertTrue(manager.securityLogs.value.isNotEmpty())
  }

  @Test
  fun stripeBillingManager_processPlanUpgrade_createsInvoiceAndLog() = runBlocking {
    val manager = StripeBillingManager()
    val initialInvoicesCount = manager.invoices.value.size
    val initialLogsCount = manager.securityLogs.value.size

    val result = manager.processSubscriptionChange(
      newTier = SubscriptionTier.ENTERPRISE,
      cycle = BillingCycle.ANNUAL,
      cardNumber = "4242 4242 4242 4242",
      cardExpiry = "12/28",
      cardCvc = "123",
      cardholderName = "Jane Dev"
    )

    assertTrue(result.isSuccess)
    assertEquals(SubscriptionTier.ENTERPRISE, manager.subscription.value.tier)
    assertEquals(BillingCycle.ANNUAL, manager.subscription.value.billingCycle)
    assertEquals(initialInvoicesCount + 1, manager.invoices.value.size)
    assertEquals(initialLogsCount + 1, manager.securityLogs.value.size)
  }

  @Test
  fun stripeBillingManager_gdprExport_containsStructuredMetadata() {
    val manager = StripeBillingManager()
    val json = manager.exportUserDataJson()
    assertNotNull(json)
    assertTrue(json.contains("customer_id"))
    assertTrue(json.contains("AES-256-GCM"))
    assertTrue(json.contains("TLS 1.3 Strict"))
  }
}

