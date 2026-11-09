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
import com.example.model.Invoice

/** Sample invoice viewer placeholder; no real or verified receipt is represented. */
@Composable
fun InvoiceReceiptDialog(invoice: Invoice, onDismiss: () -> Unit) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
    ) {
      Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Receipt preview unavailable", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("This prototype does not load or verify real invoices. No payment should be inferred from this sample record.", style = MaterialTheme.typography.bodyMedium)
        Text("Reference: ${invoice.invoiceNumber}", style = MaterialTheme.typography.bodySmall)
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Close") }
      }
    }
  }
}
