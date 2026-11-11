package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserSubscription
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.WarningAmber

@Composable
fun PauseSubscriptionDialog(
  subscription: UserSubscription,
  onDismiss: () -> Unit,
  onConfirmPause: (weeks: Int, reason: String) -> Unit,
) {
  var selectedWeeks by remember { mutableIntStateOf(4) }
  var pauseReason by remember { mutableStateOf("Temporary project hiatus & workload break") }

  val durationOptions = listOf(
    Pair(2, "2 Weeks"),
    Pair(4, "4 Weeks (1 Mo)"),
    Pair(8, "8 Weeks (2 Mo)"),
    Pair(12, "12 Weeks (3 Mo)")
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("pause_subscription_dialog"),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(WarningAmber.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PauseCircle,
            contentDescription = null,
            tint = WarningAmber,
            modifier = Modifier.size(22.dp)
          )
        }
        Column {
          Text(
            text = "Pause Subscription",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Stripe Automated Invoicing Hold",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Info card explaining Stripe webhook behavior
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Stripe Webhook Trigger Notice",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = CyanAccent
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Pausing temporarily halts recurring billing via a customer.subscription.paused webhook event with collection behavior set to void. Your projects, API credentials, and seats remain intact.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 17.sp
            )
          }
        }

        // Duration selector chips
        Text(
          text = "Select Pause Duration:",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.SemiBold
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          durationOptions.forEach { (weeks, label) ->
            val isSelected = selectedWeeks == weeks
            Surface(
              modifier = Modifier
                .weight(1f)
                .clickable { selectedWeeks = weeks }
                .testTag("pause_duration_${weeks}w"),
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) IndigoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
              border = BorderStroke(
                if (isSelected) 1.5.dp else 1.dp,
                if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
              )
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }

        // Reason input
        OutlinedTextField(
          value = pauseReason,
          onValueChange = { pauseReason = it },
          label = { Text("Pause Reason (Optional)") },
          placeholder = { Text("e.g. Vacation hiatus, budget adjustment") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pause_reason_input"),
          singleLine = true,
          shape = RoundedCornerShape(10.dp)
        )

        // Webhook event summary preview
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF080D1A),
          border = BorderStroke(1.dp, Color(0xFF283654))
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "TRIGGER PAYLOAD PREVIEW",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = WarningAmber
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "event: customer.subscription.paused\nsub_id: ${subscription.stripeSubscriptionId}\npause_collection: { behavior: \"void\", duration: \"${selectedWeeks}w\" }",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFFCBD5E1)
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirmPause(selectedWeeks, pauseReason) },
        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("confirm_pause_button")
      ) {
        Text("Confirm Pause", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("cancel_pause_button")
      ) {
        Text("Keep Subscription Active")
      }
    }
  )
}
