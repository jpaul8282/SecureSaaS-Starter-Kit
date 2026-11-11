package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PayoutDestination
import com.example.model.StripeBalance
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LivePayoutCard(
  stripeBalance: StripeBalance,
  defaultDestination: PayoutDestination?,
  onRequestPayout: () -> Unit,
  onOpenPayoutHub: () -> Unit,
  onSimulateDeposit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  // Pulsing green live indicator
  val infiniteTransition = rememberInfiniteTransition(label = "payout_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("live_payout_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top header with Stripe Connect Live Badge & Connected Account ID
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .background(
                Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF06B6D4))),
                RoundedCornerShape(10.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.AccountBalance,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Live Payouts & Balance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(8.dp))
              // Green Live Dot
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(SuccessGreen.copy(alpha = pulseAlpha), CircleShape)
              )
            }
            Text(
              text = "Stripe Connect • ${stripeBalance.connectedAccountId}",
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              color = CyanAccent
            )
          }
        }

        // Status Pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SuccessGreen.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SuccessGreen,
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "LIVE ACTIVE",
              color = SuccessGreen,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

      // Main Balance Display: Available for Transfer vs Pending
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Available Balance Tile
        Surface(
          modifier = Modifier.weight(1.3f),
          shape = RoundedCornerShape(16.dp),
          color = DeepNavyDark,
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF283654))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "AVAILABLE TO PAYOUT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent,
                letterSpacing = 0.5.sp
              )
              Icon(
                Icons.Default.ElectricBolt,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(14.dp)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = currencyFormat.format(stripeBalance.availableAmount),
              fontSize = 24.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Ready for Instant or ACH transfer",
              fontSize = 10.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        // Secondary Metrics Tile: Pending & In-Transit
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
          ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
              Text(
                text = "Pending Settlement",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = currencyFormat.format(stripeBalance.pendingAmount),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = WarningAmber.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f))
          ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
              Text(
                text = "In-Transit Payouts",
                fontSize = 10.sp,
                color = WarningAmber,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = currencyFormat.format(stripeBalance.inTransitAmount),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = WarningAmber
              )
            }
          }
        }
      }

      // Default Bank Destination Info
      defaultDestination?.let { dest ->
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
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
                tint = IndigoPrimary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "${dest.bankName} •••• ${dest.last4}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Default Destination • ${stripeBalance.payoutSchedule}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Text(
              text = "Verified",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = SuccessGreen
            )
          }
        }
      }

      // Action Buttons: Transfer Now, Payout Hub, Simulate
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onRequestPayout,
          modifier = Modifier
            .weight(1.2f)
            .height(42.dp)
            .testTag("transfer_payout_button"),
          colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Transfer Payout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        OutlinedButton(
          onClick = onOpenPayoutHub,
          modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .testTag("open_payout_hub_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Payout Hub", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
        }

        // Fast simulation chip (+sales proceeds)
        Surface(
          modifier = Modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSimulateDeposit() }
            .testTag("simulate_sales_deposit_button"),
          shape = RoundedCornerShape(12.dp),
          color = CyanAccent.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))
        ) {
          Box(
            modifier = Modifier.padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Add, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(14.dp))
              Text("+$850", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
          }
        }
      }
    }
  }
}
