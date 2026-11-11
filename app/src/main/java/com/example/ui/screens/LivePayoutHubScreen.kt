package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PayoutDestination
import com.example.model.PayoutStatus
import com.example.model.PayoutType
import com.example.model.StripePayout
import com.example.service.StripeBillingManager
import com.example.ui.components.RequestPayoutDialog
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivePayoutHubScreen(
  billingManager: StripeBillingManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

  val stripeBalance by billingManager.stripeBalance.collectAsState()
  val destinations by billingManager.payoutDestinations.collectAsState()
  val payouts by billingManager.payouts.collectAsState()

  var showPayoutDialog by remember { mutableStateOf(false) }
  var showAddBankDialog by remember { mutableStateOf(false) }
  var selectedFilterStatus by remember { mutableStateOf<PayoutStatus?>(null) }

  // New bank account state
  var newBankName by remember { mutableStateOf("Wells Fargo Business") }
  var newRouting by remember { mutableStateOf("121000241") }
  var newAccountLast4 by remember { mutableStateOf("7712") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Live Payouts & Connect",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Stripe Connect • ${stripeBalance.connectedAccountId}",
              style = MaterialTheme.typography.bodySmall,
              fontFamily = FontFamily.Monospace,
              color = CyanAccent,
              fontSize = 11.sp
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("payout_hub_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = {
              billingManager.simulateIncomingSalesDeposit(850.00)
              Toast.makeText(context, "+$850.00 incoming customer sales settled", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.testTag("payout_topbar_deposit_button")
          ) {
            Icon(
              Icons.Default.Add,
              contentDescription = "Simulate Customer Sales Deposit",
              tint = CyanAccent
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("live_payout_hub_screen"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Balance Overview Master Card
      item {
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("payout_balance_master_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "TOTAL STRIPE CONNECT BALANCE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = currencyFormat.format(stripeBalance.availableAmount + stripeBalance.pendingAmount),
                  fontSize = 28.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = SuccessGreen.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f))
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("TRANSFERS ENABLED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                }
              }
            }

            // Available vs Pending vs In-Transit Grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Available
              Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = DeepNavyDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF283654))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text("Available", fontSize = 11.sp, color = CyanAccent, fontWeight = FontWeight.SemiBold)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = currencyFormat.format(stripeBalance.availableAmount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  Text("Ready for transfer", fontSize = 9.sp, color = Color(0xFF94A3B8))
                }
              }

              // Pending
              Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text("Pending", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = currencyFormat.format(stripeBalance.pendingAmount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text("Clearing (1–2 days)", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }

              // In Transit
              Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = WarningAmber.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text("In Transit", fontSize = 11.sp, color = WarningAmber, fontWeight = FontWeight.SemiBold)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = currencyFormat.format(stripeBalance.inTransitAmount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningAmber
                  )
                  Text("Transferring now", fontSize = 9.sp, color = WarningAmber.copy(alpha = 0.8f))
                }
              }
            }

            // Primary Transfer CTA Button
            Button(
              onClick = { showPayoutDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("hub_initiate_payout_button"),
              colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Request Live Payout Transfer", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 2. Automated Payout Schedule Configuration
      item {
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("payout_schedule_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stripe Payout Schedule", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
              Text(
                text = stripeBalance.payoutSchedule,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent
              )
            }

            Text(
              text = "Choose whether Stripe automatically deposits funds on a recurring cadence or waits for manual on-demand payouts.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf("Daily (Automatic)", "Weekly (Mondays)", "Manual Only").forEach { scheduleOption ->
                val isSelected = stripeBalance.payoutSchedule.startsWith(scheduleOption.take(5))
                Surface(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                      billingManager.updatePayoutSchedule(scheduleOption)
                      Toast.makeText(context, "Payout schedule set to $scheduleOption", Toast.LENGTH_SHORT).show()
                    },
                  shape = RoundedCornerShape(10.dp),
                  color = if (isSelected) IndigoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                  border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                  )
                ) {
                  Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp), contentAlignment = Alignment.Center) {
                    Text(
                      text = scheduleOption,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                      color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 3. Connected Bank Accounts & Debit Cards
      item {
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("payout_destinations_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Payout Destination Accounts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              }
              OutlinedButton(
                onClick = { showAddBankDialog = true },
                modifier = Modifier.testTag("add_bank_button"),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Link Bank", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            destinations.forEach { dest ->
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .background(if (dest.type == "DEBIT_CARD") CyanAccent.copy(alpha = 0.15f) else IndigoPrimary.copy(alpha = 0.15f), CircleShape),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = if (dest.type == "DEBIT_CARD") Icons.Default.CreditCard else Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = if (dest.type == "DEBIT_CARD") CyanAccent else IndigoPrimary,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = dest.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        if (dest.isDefault) {
                          Spacer(modifier = Modifier.width(6.dp))
                          Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = IndigoPrimary.copy(alpha = 0.15f)
                          ) {
                            Text(
                              text = "DEFAULT",
                              color = IndigoPrimary,
                              fontSize = 9.sp,
                              fontWeight = FontWeight.Bold,
                              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                          }
                        }
                      }
                      Text(
                        text = "${dest.bankName} •••• ${dest.last4} • ${if (dest.instantPayoutEligible) "Instant Eligible" else "Standard ACH"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  Text(
                    text = "Active",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                  )
                }
              }
            }
          }
        }
      }

      // 4. Live Payouts History Section
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Payout History (${payouts.size})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Electronic funds transfers processed through Stripe",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          TextButton(
            onClick = {
              val historyJson = payouts.joinToString(separator = "\n") {
                "${it.id},${it.amount},${it.status},${it.createdDate},${it.traceId}"
              }
              Toast.makeText(context, "Exported ${payouts.size} payout records", Toast.LENGTH_SHORT).show()
            }
          ) {
            Text("Export CSV", fontWeight = FontWeight.Bold, color = IndigoPrimary)
          }
        }
      }

      // Filter Chips
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(null to "All", PayoutStatus.PAID to "Paid", PayoutStatus.IN_TRANSIT to "In Transit").forEach { (status, label) ->
            val isSelected = selectedFilterStatus == status
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { selectedFilterStatus = status },
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
              Text(
                text = label,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      // Payouts List Items
      val filteredPayouts = payouts.filter { selectedFilterStatus == null || it.status == selectedFilterStatus }
      items(filteredPayouts, key = { it.id }) { payout ->
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("payout_item_${payout.id}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = currencyFormat.format(payout.amount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (payout.type == PayoutType.INSTANT) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = CyanAccent.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = "INSTANT",
                        color = CyanAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
                Text(
                  text = "${payout.destination.name} •••• ${payout.destination.last4}",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Status Pill
              val (badgeText, badgeColor) = when (payout.status) {
                PayoutStatus.PAID -> "PAID" to SuccessGreen
                PayoutStatus.IN_TRANSIT -> "IN TRANSIT" to WarningAmber
                PayoutStatus.PENDING -> "PENDING" to CyanAccent
                PayoutStatus.FAILED -> "FAILED" to ErrorRed
                PayoutStatus.CANCELED -> "CANCELED" to Color.Gray
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = badgeColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
              ) {
                Text(
                  text = badgeText,
                  color = badgeColor,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            // Metadata footer: Trace ID & Timestamps
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Created: ${payout.createdDate}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Trace: ${payout.traceId}",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // If In-Transit, button to simulate settlement
              if (payout.status == PayoutStatus.IN_TRANSIT) {
                Button(
                  onClick = {
                    billingManager.markPayoutPaid(payout.id)
                    Toast.makeText(context, "ACH Transfer ${payout.id} marked as settled", Toast.LENGTH_SHORT).show()
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  modifier = Modifier.testTag("settle_payout_${payout.id}")
                ) {
                  Text("Settle Funds", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }

  // Request Payout Dialog
  if (showPayoutDialog) {
    RequestPayoutDialog(
      stripeBalance = stripeBalance,
      destinations = destinations,
      onDismiss = { showPayoutDialog = false },
      onConfirmPayout = { amount, destId, isInstant ->
        scope.launch {
          val result = billingManager.requestLivePayout(amount, destId, isInstant)
          showPayoutDialog = false
          if (result.isSuccess) {
            Toast.makeText(context, "Stripe payout ${result.getOrNull()?.id} dispatched successfully!", Toast.LENGTH_LONG).show()
          } else {
            Toast.makeText(context, "Payout failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
          }
        }
      }
    )
  }

  // Link New Bank Dialog
  if (showAddBankDialog) {
    AlertDialog(
      onDismissRequest = { showAddBankDialog = false },
      icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = IndigoPrimary) },
      title = { Text("Link Bank Destination Account") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Connect an external US bank account (ACH) for receiving Stripe balance transfers.")
          OutlinedTextField(
            value = newBankName,
            onValueChange = { newBankName = it },
            label = { Text("Bank Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newRouting,
            onValueChange = { newRouting = it },
            label = { Text("Routing Number (9 Digits)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newAccountLast4,
            onValueChange = { newAccountLast4 = it },
            label = { Text("Account Last 4 Digits") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newBankName.isNotBlank() && newAccountLast4.length == 4) {
              billingManager.addBankPayoutDestination(newBankName, newRouting, newAccountLast4)
              showAddBankDialog = false
              Toast.makeText(context, "Bank account linked successfully", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
          Text("Save & Verify")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddBankDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
