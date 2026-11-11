package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserSubscription
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import java.text.NumberFormat
import java.util.Locale

@Composable
fun QuotaStatusCard(
  subscription: UserSubscription,
  onSimulateApiCall: (Int) -> Unit,
  onResetUsage: () -> Unit,
  onUpgradePlan: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val currentUsage = subscription.currentPeriodUsageApi
  val maxUsage = subscription.maxUsageApi.coerceAtLeast(1)
  val usageRatio = (currentUsage.toFloat() / maxUsage.toFloat()).coerceIn(0f, 1f)
  val remainingRequests = (maxUsage - currentUsage).coerceAtLeast(0)

  // Smooth animations for progress & count numbers
  val animatedProgress by animateFloatAsState(
    targetValue = usageRatio,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "quota_progress"
  )

  val animatedRemaining by animateIntAsState(
    targetValue = remainingRequests,
    animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
    label = "remaining_requests_count"
  )

  // Dynamic status thresholds
  val (statusLabel, statusColor, statusBg) = when {
    usageRatio >= 0.95f -> Triple("CRITICAL LIMIT", ErrorRed, ErrorRed.copy(alpha = 0.15f))
    usageRatio >= 0.80f -> Triple("HIGH USAGE", WarningAmber, WarningAmber.copy(alpha = 0.15f))
    usageRatio >= 0.50f -> Triple("MODERATE", CyanAccent, CyanAccent.copy(alpha = 0.15f))
    else -> Triple("HEALTHY", SuccessGreen, SuccessGreen.copy(alpha = 0.15f))
  }

  val progressIndicatorColor by animateColorAsState(
    targetValue = when {
      usageRatio >= 0.95f -> ErrorRed
      usageRatio >= 0.80f -> WarningAmber
      else -> CyanAccent
    },
    animationSpec = tween(durationMillis = 500),
    label = "progress_indicator_color"
  )

  // Infinite pulsing animation for live badge indicator
  val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  val numberFormat = NumberFormat.getNumberInstance(Locale.US)

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("quota_status_card"),
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
      // Header: Title, Live indicator & Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .background(IndigoPrimary.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Speed,
              contentDescription = null,
              tint = IndigoPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Quota Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(8.dp))
              // Live pulsing dot
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(statusColor.copy(alpha = pulseAlpha), CircleShape)
              )
            }
            Text(
              text = "Monthly API calls • ${subscription.tier.title} Plan",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Status Pill
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = statusBg,
          border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (usageRatio >= 0.80f) Icons.Default.Warning else Icons.Default.CheckCircle,
              contentDescription = null,
              tint = statusColor,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = statusLabel,
              color = statusColor,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

      // Main Visuals: Circular Progress Meter & Remaining Requests Stats
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // 1. Circular Progress Indicator with embedded Percentage
        Box(
          modifier = Modifier
            .size(112.dp)
            .testTag("quota_circular_progress"),
          contentAlignment = Alignment.Center
        ) {
          // Background Track
          CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(112.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            strokeWidth = 10.dp,
            strokeCap = StrokeCap.Round
          )

          // Animated Foreground Progress
          CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(112.dp),
            color = progressIndicatorColor,
            strokeWidth = 10.dp,
            strokeCap = StrokeCap.Round
          )

          // Center Metrics
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = "${(animatedProgress * 100).toInt()}%",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "USED",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // 2. Animated Remaining Count & Quota Breakdown
        Column(
          modifier = Modifier
            .weight(1f)
            .testTag("quota_remaining_count")
        ) {
          Text(
            text = "REMAINING REQUESTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          // Animated number display
          AnimatedContent(
            targetState = animatedRemaining,
            transitionSpec = {
              if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn())
                  .togetherWith(slideOutVertically { height -> -height } + fadeOut())
              } else {
                (slideInVertically { height -> -height } + fadeIn())
                  .togetherWith(slideOutVertically { height -> height } + fadeOut())
              }
            },
            label = "animated_remaining_counter"
          ) { count ->
            Text(
              text = numberFormat.format(count),
              fontSize = 24.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (usageRatio >= 0.95f) ErrorRed else IndigoPrimary,
              fontFamily = FontFamily.Default
            )
          }

          Text(
            text = "of ${numberFormat.format(maxUsage)} monthly limit",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Consumption sub-bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Consumed: ${numberFormat.format(currentUsage)}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Cycle: ${subscription.renewalDate}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 3. Quick Simulation & Management Action Chips
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Live Quota Controls",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            // Reset meter icon button
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Reset Cycle",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              IconButton(
                onClick = onResetUsage,
                modifier = Modifier
                  .size(28.dp)
                  .testTag("quota_reset_button")
              ) {
                Icon(
                  Icons.Default.Refresh,
                  contentDescription = "Reset quota to 0",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }

          // Simulation trigger buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = { onSimulateApiCall(1500) },
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("quota_simulate_call_button"),
              shape = RoundedCornerShape(10.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
            ) {
              Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp), tint = CyanAccent)
              Spacer(modifier = Modifier.width(4.dp))
              Text("+1.5k Calls", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
              onClick = { onSimulateApiCall(10000) },
              modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .testTag("quota_simulate_burst_button"),
              shape = RoundedCornerShape(10.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = IndigoPrimary)
              Spacer(modifier = Modifier.width(4.dp))
              Text("+10k Burst", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            if (usageRatio >= 0.80f) {
              Button(
                onClick = onUpgradePlan,
                modifier = Modifier
                  .weight(1.1f)
                  .height(38.dp)
                  .testTag("quota_upgrade_button"),
                colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
              ) {
                Text("Expand Quota", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Black)
              }
            }
          }
        }
      }
    }
  }
}
