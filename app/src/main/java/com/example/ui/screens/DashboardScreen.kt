package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Invoice
import com.example.service.StripeBillingManager

@Composable
fun DashboardScreen(
  billingManager: StripeBillingManager,
  onNavigateToPlans: () -> Unit,
  @Suppress("UNUSED_PARAMETER") onNavigateToHistory: () -> Unit,
  @Suppress("UNUSED_PARAMETER") onNavigateToSecurity: () -> Unit,
  @Suppress("UNUSED_PARAMETER") onViewInvoice: (Invoice) -> Unit,
  onOpenStripeSettings: () -> Unit,
) {
  val subscription by billingManager.subscription.collectAsState()

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
          Text("Showcase prototype", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text("Sample interface only. There is no live Stripe connection, real subscription, or production security service.", style = MaterialTheme.typography.bodyMedium)
        }
      }
    }
    item {
      Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Illustrative dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
          Text("Sample state: ${subscription.tier.title} · ${subscription.status}", style = MaterialTheme.typography.bodyLarge)
          Text("Values are local demo content and are not retrieved from a customer account.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
    item { Button(onClick = onNavigateToPlans, modifier = Modifier.fillMaxWidth()) { Text("View plan examples") } }
    item { Button(onClick = onOpenStripeSettings, modifier = Modifier.fillMaxWidth()) { Text("Integration status") } }
  }
}
