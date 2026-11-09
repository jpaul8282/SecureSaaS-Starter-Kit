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
import com.example.service.StripeBillingManager

@Composable
fun UserGuideScreen() {
  LazyColumn(
    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item { Text("About this app", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
      ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Showcase prototype", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          Text("This app demonstrates Android screen layouts with local sample state only. It is not a live billing, payment, account, privacy, or security service. Do not enter real payment or personal data.")
        }
      }
    }
    item {
      Text("Plan prices, usage figures, invoices, and status examples are illustrative. No Stripe connection, real webhook verification, account deletion, data export, or production encryption/audit service is provided.", style = MaterialTheme.typography.bodyMedium)
    }
    item {
      Text("Developers: read the repository README and docs/PLAY_STORE_READINESS.md before building on this prototype or preparing a store release.", style = MaterialTheme.typography.bodyMedium)
    }
  }
}
