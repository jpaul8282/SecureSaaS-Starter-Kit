package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Invoice
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen
import com.example.util.PdfReceiptGenerator
import java.io.File

@Composable
fun InvoiceReceiptDialog(
  invoice: Invoice,
  onDismiss: () -> Unit,
) {
  val context = LocalContext.current
  var downloadedFile by remember { mutableStateOf<File?>(null) }
  var isGeneratingPdf by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(16.dp)
        .testTag("invoice_receipt_dialog"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
      )
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(SuccessGreen.copy(alpha = 0.15f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.AutoMirrored.Filled.ReceiptLong,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Official Stripe Receipt",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = invoice.invoiceNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Amount Hero
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              RoundedCornerShape(16.dp)
            )
            .border(
              1.dp,
              MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
              RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "AMOUNT PAID",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = invoice.amount,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SuccessGreen
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
          ) {
            Icon(
              Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SuccessGreen,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Payment Cleared (${invoice.date})",
              style = MaterialTheme.typography.bodySmall,
              color = SuccessGreen
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Receipt Details
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ReceiptRow(label = "Plan", value = invoice.planName)
          ReceiptRow(label = "Billing Period", value = invoice.billingPeriod)
          ReceiptRow(label = "Stripe Reference", value = invoice.id, isMonospace = true)
          ReceiptRow(label = "Payment Status", value = invoice.status)
          ReceiptRow(label = "Payment Method", value = invoice.paymentMethod)
          ReceiptRow(label = "Customer ID", value = "cus_R9x2Kl84mQpZ01", isMonospace = true)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Download Status feedback if generated
        if (downloadedFile != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(SuccessGreen.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
              .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "PDF saved: ${downloadedFile?.name}",
                  fontSize = 11.sp,
                  color = SuccessGreen,
                  fontWeight = FontWeight.Medium
                )
              }
              IconButton(
                onClick = {
                  downloadedFile?.let { f ->
                    try {
                      context.startActivity(PdfReceiptGenerator.sharePdfIntent(context, f))
                    } catch (e: Exception) {
                      Toast.makeText(context, "Cannot share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                  }
                },
                modifier = Modifier.size(30.dp)
              ) {
                Icon(Icons.Default.Share, contentDescription = "Share PDF", tint = SuccessGreen, modifier = Modifier.size(16.dp))
              }
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Download PDF Receipt & Done
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              isGeneratingPdf = true
              try {
                val file = PdfReceiptGenerator.generateReceiptPdf(context, invoice)
                downloadedFile = file
                Toast.makeText(context, "Receipt PDF saved to Downloads!", Toast.LENGTH_SHORT).show()
                try {
                  context.startActivity(PdfReceiptGenerator.openPdfIntent(context, file))
                } catch (e: Exception) {
                  // If no PDF viewer app is installed, user still has the saved file
                }
              } catch (e: Exception) {
                Toast.makeText(context, "Failed to create PDF: ${e.message}", Toast.LENGTH_LONG).show()
              } finally {
                isGeneratingPdf = false
              }
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("download_pdf_receipt_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (downloadedFile != null) "View / Re-download" else "Download PDF")
          }

          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier
              .weight(0.6f)
              .height(48.dp)
              .testTag("dismiss_receipt_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Done")
          }
        }
      }
    }
  }
}

@Composable
private fun ReceiptRow(label: String, value: String, isMonospace: Boolean = false) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.SemiBold,
      fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
