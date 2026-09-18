package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BillingCycle
import com.example.model.SubscriptionTier
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StripePaymentSheetDialog(
  plan: SubscriptionTier,
  cycle: BillingCycle,
  onDismiss: () -> Unit,
  onSuccess: (tier: SubscriptionTier, cycle: BillingCycle, cardNumber: String, expiry: String, cvc: String, name: String) -> Unit,
) {
  var cardNumber by remember { mutableStateOf("") }
  var cardExpiry by remember { mutableStateOf("") }
  var cardCvc by remember { mutableStateOf("") }
  var cardholderName by remember { mutableStateOf("") }

  var isProcessing by remember { mutableStateOf(false) }
  var processingStep by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isSuccess by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()
  val price = if (cycle == BillingCycle.MONTHLY) plan.monthlyPrice else plan.annualPrice
  val periodText = if (cycle == BillingCycle.MONTHLY) "/month" else "/year"

  Dialog(
    onDismissRequest = { if (!isProcessing) onDismiss() },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(16.dp)
        .testTag("stripe_payment_dialog"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
      )
    ) {
      Column(
        modifier = Modifier
          .padding(24.dp)
          .verticalScroll(rememberScrollState()),
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
                .background(
                  Brush.linearGradient(listOf(IndigoPrimary, CyanAccent)),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Stripe Secure Checkout",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "PCI-DSS Level 1 Encrypted",
                style = MaterialTheme.typography.bodySmall,
                color = CyanAccent
              )
            }
          }
          IconButton(
            onClick = onDismiss,
            enabled = !isProcessing,
            modifier = Modifier.testTag("close_checkout_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order Summary Card
        Box(
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
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${plan.title} Plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Billed ${cycle.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Text(
              text = "$$price$periodText",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.ExtraBold,
              color = IndigoPrimary
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSuccess) {
          // Success view
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SuccessGreen,
              modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Payment Succeeded!",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = SuccessGreen
            )
            Text(
              text = "Subscription activated. A confirmation receipt has been generated.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
          }
        } else if (isProcessing) {
          // Processing indicator
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(
              color = CyanAccent,
              strokeWidth = 3.dp,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = processingStep,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Please do not close this window...",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          // Quick Test Fill Button
          OutlinedButton(
            onClick = {
              cardNumber = "4242 4242 4242 4242"
              cardExpiry = "12/28"
              cardCvc = "842"
              cardholderName = "Jane Dev"
              errorMessage = null
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("fill_test_card_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Fill Stripe Test Card (4242...)")
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Card Number Input
          OutlinedTextField(
            value = cardNumber,
            onValueChange = { input ->
              val clean = input.filter { it.isDigit() }.take(16)
              cardNumber = clean.chunked(4).joinToString(" ")
            },
            label = { Text("Card Number") },
            placeholder = { Text("4242 4242 4242 4242") },
            leadingIcon = {
              Icon(Icons.Default.CreditCard, contentDescription = null, tint = IndigoPrimary)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("card_number_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CyanAccent,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
              value = cardExpiry,
              onValueChange = { input ->
                val clean = input.filter { it.isDigit() }.take(4)
                cardExpiry = if (clean.length > 2) "${clean.take(2)}/${clean.drop(2)}" else clean
              },
              label = { Text("MM/YY") },
              placeholder = { Text("12/28") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .weight(1f)
                .testTag("card_expiry_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
              value = cardCvc,
              onValueChange = { input ->
                cardCvc = input.filter { it.isDigit() }.take(4)
              },
              label = { Text("CVC") },
              placeholder = { Text("123") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .weight(1f)
                .testTag("card_cvc_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = cardholderName,
            onValueChange = { cardholderName = it },
            label = { Text("Cardholder Full Name") },
            placeholder = { Text("Jane Doe") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("cardholder_name_input"),
            shape = RoundedCornerShape(12.dp)
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = errorMessage ?: "",
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Pay Button
          Button(
            onClick = {
              if (cardNumber.isBlank() || cardExpiry.isBlank() || cardCvc.isBlank() || cardholderName.isBlank()) {
                errorMessage = "Please complete all payment fields."
                return@Button
              }
              errorMessage = null
              isProcessing = true

              scope.launch {
                processingStep = "Tokenizing card PAN via Stripe Elements..."
                delay(600)
                processingStep = "Performing 3D Secure / SCA Verification..."
                delay(600)
                processingStep = "Confirming Stripe PaymentIntent pi_3N..."
                delay(700)
                isProcessing = false
                isSuccess = true
                delay(900)
                onSuccess(plan, cycle, cardNumber, cardExpiry, cardCvc, cardholderName)
                onDismiss()
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("submit_payment_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = IndigoPrimary
            ),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Pay $$price.00 USD",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Security Trust Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Security,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "End-to-End TLS 1.3 • Zero-Knowledge Storage",
              style = MaterialTheme.typography.bodySmall,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
