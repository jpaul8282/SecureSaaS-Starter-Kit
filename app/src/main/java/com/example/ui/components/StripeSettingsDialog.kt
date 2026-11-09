package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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

/** Prototype information panel; it does not configure Stripe or send webhook events. */
@Composable
fun StripeSettingsDialog(
  @Suppress("UNUSED_PARAMETER") currentKey: String,
  onDismiss: () -> Unit,
  @Suppress("UNUSED_PARAMETER") onSaveKey: (String) -> Unit,
  @Suppress("UNUSED_PARAMETER") onTriggerWebhook: (String) -> Unit,
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text("Integration status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
          "Demo prototype — Stripe is not connected. No API key is configured or stored here, and webhook events are not received or verified. A production integration requires a trusted backend.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Close") }
      }
    }
  }
}
