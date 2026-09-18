package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BillingCycle
import com.example.model.SubscriptionTier
import com.example.service.StripeBillingManager
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen

@Composable
fun PlansBillingScreen(
  billingManager: StripeBillingManager,
  onSelectPlanForCheckout: (SubscriptionTier, BillingCycle) -> Unit,
) {
  val subscription by billingManager.subscription.collectAsState()
  val billingCycle by billingManager.billingCycle.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("plans_billing_screen"),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Illustration Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp)
              .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_stripe_billing),
              contentDescription = "Stripe Billing Plans",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xDD0A0F1D))
                  )
                )
            )
            Column(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
            ) {
              Text(
                text = "Seamless Stripe Subscriptions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Automated Recurring Billing • Instant SCA Checkout",
                style = MaterialTheme.typography.bodySmall,
                color = CyanAccent
              )
            }
          }
        }
      }
    }

    // Billing Cycle Switcher (Monthly vs Annual)
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Billing Frequency",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (billingCycle == BillingCycle.ANNUAL) "Annual billing selected (Save 20%)" else "Monthly recurring subscription",
              style = MaterialTheme.typography.bodySmall,
              color = if (billingCycle == BillingCycle.ANNUAL) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Monthly",
              fontSize = 13.sp,
              color = if (billingCycle == BillingCycle.MONTHLY) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = if (billingCycle == BillingCycle.MONTHLY) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
              checked = billingCycle == BillingCycle.ANNUAL,
              onCheckedChange = { checked ->
                billingManager.setBillingCycle(if (checked) BillingCycle.ANNUAL else BillingCycle.MONTHLY)
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = IndigoPrimary
              ),
              modifier = Modifier.testTag("billing_cycle_switch")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Annual",
              fontSize = 13.sp,
              color = if (billingCycle == BillingCycle.ANNUAL) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = if (billingCycle == BillingCycle.ANNUAL) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }

    // Plans list
    items(SubscriptionTier.values().toList()) { tier ->
      val isCurrentPlan = subscription.tier == tier
      val price = if (billingCycle == BillingCycle.MONTHLY) tier.monthlyPrice else tier.annualPrice
      val period = if (billingCycle == BillingCycle.MONTHLY) "/month" else "/year"

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("plan_card_${tier.name.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (tier.isPopular) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
          if (tier.isPopular) 2.dp else 1.dp,
          if (tier.isPopular) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = tier.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = tier.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
              )
            }
            if (tier.isPopular) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = IndigoPrimary.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.Star, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "MOST POPULAR",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = IndigoPrimary
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Price Tag
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = if (price == 0) "$0" else "$$price",
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (price == 0) " Free forever" else " $period",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(bottom = 6.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Features List
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tier.features.forEach { feature ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = if (tier.isPopular) CyanAccent else SuccessGreen,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = feature,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Action Button
          if (isCurrentPlan) {
            OutlinedButton(
              onClick = { /* Already active */ },
              enabled = false,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Current Active Plan")
            }
          } else {
            Button(
              onClick = { onSelectPlanForCheckout(tier, billingCycle) },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("select_plan_button_${tier.name.lowercase()}"),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (tier.isPopular) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (tier == SubscriptionTier.STARTER) "Downgrade to Starter" else "Subscribe via Stripe",
                fontWeight = FontWeight.Bold,
                color = if (tier.isPopular) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}
