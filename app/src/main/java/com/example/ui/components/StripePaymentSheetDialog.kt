package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BillingCycle
import com.example.model.SubscriptionTier

/** A truthful placeholder, not a payment form. Wire a backend and provider SDK before enabling checkout. */
@Composable
fun StripePaymentSheetDialog(
  plan: SubscriptionTier,
  cycle: BillingCycle,
  onDismiss: () -> Unit,
  @Suppress("UNUSED_PARAMETER") onSuccess: (tier: SubscriptionTier, cycle: BillingCycle, cardNumber: String, expiry: String, cvc: String, name: String) -> Unit,
) {
  val price = if (cycle == BillingCycle.MONTHLY) plan.monthlyPrice else plan.annualPrice
  val interval = if (cycle == BillingCycle.MONTHLY) "month" else "year"

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      modifier = Modifier.fillMaxWidth(0.94f).padding(16.dp),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text("Checkout preview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("${plan.title} · ${cycle.label} · $$price/$interval", style = MaterialTheme.typography.titleMedium)
        Text(
          "Demo only — no payment is processed and no subscription is created. This prototype has no billing backend. Do not enter card details. The displayed plan and price are illustrative.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
          Text("Close preview")
        }
      }
    }
  }
}
