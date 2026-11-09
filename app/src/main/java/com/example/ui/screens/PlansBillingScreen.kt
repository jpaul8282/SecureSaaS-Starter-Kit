package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.BillingCycle
import com.example.model.SubscriptionTier
import com.example.service.StripeBillingManager

@Composable
fun PlansBillingScreen(
  billingManager: StripeBillingManager,
  onSelectPlanForCheckout: (SubscriptionTier, BillingCycle) -> Unit,
) {
  val subscription by billingManager.subscription.collectAsState()
  val billingCycle by billingManager.billingCycle.collectAsState()

  LazyColumn(
    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
      ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Plan examples — not for purchase", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text("Prices and features are illustrative. No checkout or subscription is available in this prototype.", style = MaterialTheme.typography.bodyMedium)
        }
      }
    }
    item {
      OutlinedButton(
        onClick = { billingManager.setBillingCycle(if (billingCycle == BillingCycle.MONTHLY) BillingCycle.ANNUAL else BillingCycle.MONTHLY) },
        modifier = Modifier.fillMaxWidth(),
      ) { Text("Billing period: ${billingCycle.label} (tap to switch)") }
    }
    items(SubscriptionTier.values().toList()) { tier ->
      val price = if (billingCycle == BillingCycle.MONTHLY) tier.monthlyPrice else tier.annualPrice
      Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(tier.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
          Text(if (price == 0) "Illustrative free tier" else "Illustrative price: $$price / ${if (billingCycle == BillingCycle.MONTHLY) "month" else "year"}")
          Text(tier.description, style = MaterialTheme.typography.bodyMedium)
          tier.features.forEach { feature -> Text("• $feature", style = MaterialTheme.typography.bodySmall) }
          Spacer(Modifier.height(4.dp))
          Button(
            onClick = { onSelectPlanForCheckout(tier, billingCycle) },
            enabled = tier != subscription.tier,
            modifier = Modifier.fillMaxWidth(),
          ) { Text(if (tier == subscription.tier) "Sample current plan" else "Preview only") }
        }
      }
    }
  }
}
