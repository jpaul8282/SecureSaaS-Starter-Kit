package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.PayoutDestination
import com.example.model.StripeBalance
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RequestPayoutDialog(
  stripeBalance: StripeBalance,
  destinations: List<PayoutDestination>,
  onDismiss: () -> Unit,
  onConfirmPayout: (amount: Double, destinationId: String, isInstant: Boolean) -> Unit,
) {
  var amountInput by remember { mutableStateOf("1000.00") }
  var selectedDestinationId by remember {
    mutableStateOf(destinations.firstOrNull { it.isDefault }?.id ?: destinations.firstOrNull()?.id ?: "")
  }
  var isInstantPayout by remember { mutableStateOf(false) }
  var isProcessing by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val scope = rememberCoroutineScope()
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  val enteredAmount = amountInput.toDoubleOrNull() ?: 0.0
  val selectedDest = destinations.find { it.id == selectedDestinationId }
  val fee = if (isInstantPayout) enteredAmount * 0.015 else 0.0
  val netAmount = (enteredAmount - fee).coerceAtLeast(0.0)

  Dialog(
    onDismissRequest = { if (!isProcessing) onDismiss() },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 24.dp)
        .testTag("request_payout_dialog"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                .size(40.dp)
                .background(IndigoPrimary.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.AccountBalance,
                contentDescription = null,
                tint = IndigoPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Initiate Live Payout",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Stripe Connect Transfer",
                style = MaterialTheme.typography.bodySmall,
                color = CyanAccent,
                fontSize = 11.sp
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            enabled = !isProcessing,
            modifier = Modifier.testTag("payout_dialog_close")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        // Available Balance Notice Banner
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Available to Transfer",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = currencyFormat.format(stripeBalance.availableAmount),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SuccessGreen
              )
            }
            TextButton(
              onClick = {
                amountInput = String.format(Locale.US, "%.2f", stripeBalance.availableAmount)
              },
              enabled = !isProcessing
            ) {
              Text("Transfer Max", fontWeight = FontWeight.Bold, color = IndigoPrimary)
            }
          }
        }

        // Amount Input Field
        Column {
          Text(
            text = "Transfer Amount (USD)",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = amountInput,
            onValueChange = {
              amountInput = it
              errorMessage = null
            },
            leadingIcon = {
              Text(
                text = "$",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = IndigoPrimary,
                modifier = Modifier.padding(start = 12.dp)
              )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("payout_amount_input"),
            shape = RoundedCornerShape(12.dp)
          )

          // Fast amount shortcut chips
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(250.0, 500.0, 1000.0, 2500.0).forEach { chipAmount ->
              Surface(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .clickable(enabled = !isProcessing) {
                    amountInput = String.format(Locale.US, "%.2f", chipAmount)
                    errorMessage = null
                  },
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
              ) {
                Box(
                  modifier = Modifier.padding(vertical = 6.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "+$${chipAmount.toInt()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }

        // Destination Account Selector
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Destination Account",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )

          destinations.forEach { dest ->
            val isSelected = dest.id == selectedDestinationId
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = !isProcessing) {
                  selectedDestinationId = dest.id
                }
                .testTag("payout_dest_${dest.id}"),
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) IndigoPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
              )
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = if (dest.type == "DEBIT_CARD") Icons.Default.CreditCard else Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = dest.name,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = "${dest.bankName} •••• ${dest.last4} (${dest.type})",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                if (isSelected) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }

        // Transfer Speed Options (Standard ACH vs Instant Payout)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Transfer Speed",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Standard Option
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = !isProcessing) { isInstantPayout = false },
              shape = RoundedCornerShape(12.dp),
              color = if (!isInstantPayout) IndigoPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (!isInstantPayout) IndigoPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
              )
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("Standard ACH", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("1–2 Business Days", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text("FREE ($0 Fee)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
              }
            }

            // Instant Option
            val isEligible = selectedDest?.instantPayoutEligible == true
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = !isProcessing && isEligible) {
                  if (isEligible) isInstantPayout = true
                },
              shape = RoundedCornerShape(12.dp),
              color = if (isInstantPayout) CyanAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isInstantPayout) CyanAccent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
              )
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Instant Payout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text("Within 30 Minutes", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = if (isEligible) "1.5% Fee ($${String.format(Locale.US, "%.2f", enteredAmount * 0.015)})" else "Ineligible for this bank",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isEligible) CyanAccent else WarningAmber
                )
              }
            }
          }
        }

        // Summary Breakdown Card
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = DeepNavyDark,
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF283654))
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Gross Transfer:", fontSize = 12.sp, color = Color(0xFF94A3B8))
              Text(currencyFormat.format(enteredAmount), fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Stripe Payout Fee:", fontSize = 12.sp, color = Color(0xFF94A3B8))
              Text(if (fee > 0) "-${currencyFormat.format(fee)}" else "$0.00 (Free)", fontSize = 12.sp, color = if (fee > 0) WarningAmber else SuccessGreen)
            }
            HorizontalDivider(color = Color(0xFF283654), modifier = Modifier.padding(vertical = 4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Estimated Deposit (Net):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
              Text(currencyFormat.format(netAmount), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
          }
        }

        // Error message if present
        errorMessage?.let { error ->
          Text(
            text = error,
            color = ErrorRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Security Assurance Notice
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Stripe Connect TLS 1.3 encrypted transfer • Trace logged",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Submit Button
        Button(
          onClick = {
            if (enteredAmount <= 0) {
              errorMessage = "Please enter a valid transfer amount"
              return@Button
            }
            if (enteredAmount < 10.0) {
              errorMessage = "Minimum payout transfer is $10.00 USD"
              return@Button
            }
            if (enteredAmount > stripeBalance.availableAmount) {
              errorMessage = "Amount exceeds available balance of ${currencyFormat.format(stripeBalance.availableAmount)}"
              return@Button
            }

            isProcessing = true
            errorMessage = null
            onConfirmPayout(enteredAmount, selectedDestinationId, isInstantPayout)
          },
          enabled = !isProcessing && enteredAmount > 0,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("payout_submit_confirm_button"),
          colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
          shape = RoundedCornerShape(12.dp)
        ) {
          if (isProcessing) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dispatching to Stripe...", fontWeight = FontWeight.Bold)
          } else {
            Text("Confirm Payout of ${currencyFormat.format(enteredAmount)}", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
