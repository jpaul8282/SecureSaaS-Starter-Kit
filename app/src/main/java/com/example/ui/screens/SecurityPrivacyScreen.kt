package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.service.StripeBillingManager

@Composable
fun SecurityPrivacyScreen(@Suppress("UNUSED_PARAMETER") billingManager: StripeBillingManager) {
  LazyColumn(
    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item { Text("Privacy & prototype status", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
      ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Sample data only", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text("This showcase does not connect to Stripe, verify webhooks, provide production security controls, or establish regulatory compliance. Do not enter sensitive personal or payment information.")
        }
      }
    }
    item {
      Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Privacy services are not connected", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text("The prototype cannot export or delete real account data or data held by a service provider. No privacy request can be submitted or treated as complete from this screen.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }
    item {
      Text("The app has no configured analytics or account backend. Review the actual released binary and its dependencies before completing Google Play Data safety disclosures.", style = MaterialTheme.typography.bodyMedium)
    }
  }
}
