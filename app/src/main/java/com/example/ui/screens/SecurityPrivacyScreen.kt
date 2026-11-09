package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

/** Makes the prototype scope explicit; none of these cards are production security controls. */
@Composable
fun SecurityPrivacyScreen(
  @Suppress("UNUSED_PARAMETER") billingManager: StripeBillingManager,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item {
      Text("Privacy & prototype status", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
      ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Demo data only", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text(
            "This showcase uses sample billing and security records. It does not connect to Stripe, verify webhooks, provide encryption/audit guarantees, or establish GDPR/CCPA compliance. Do not enter sensitive personal or payment information.",
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }
    item {
      Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Privacy requests are not connected", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text(
            "The current prototype cannot export or delete a real account or data held by a service provider. No request should be treated as completed. Implement authenticated backend workflows before offering these controls to real users.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}
