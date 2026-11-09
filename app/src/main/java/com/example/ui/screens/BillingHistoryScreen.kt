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

@Composable
fun BillingHistoryScreen(
  @Suppress("UNUSED_PARAMETER") billingManager: com.example.service.StripeBillingManager,
  @Suppress("UNUSED_PARAMETER") onViewInvoice: (com.example.model.Invoice) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item { Text("Billing history", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
      ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("No live billing data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text("This showcase is not connected to a billing provider. No real invoices, payments, receipts, or payment methods are available.")
        }
      }
    }
  }
}
