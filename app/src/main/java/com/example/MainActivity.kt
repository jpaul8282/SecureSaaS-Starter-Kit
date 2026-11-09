package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BillingCycle
import com.example.model.Invoice
import com.example.model.SubscriptionTier
import com.example.service.StripeBillingManager
import com.example.ui.components.InvoiceReceiptDialog
import com.example.ui.components.StripePaymentSheetDialog
import com.example.ui.components.StripeSettingsDialog
import com.example.ui.screens.BillingHistoryScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PlansBillingScreen
import com.example.ui.screens.SecurityPrivacyScreen
import com.example.ui.screens.UserGuideScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { MyApplicationTheme { BillingHubApp() } }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingHubApp() {
  val billingManager = remember { StripeBillingManager() }
  var currentTab by remember { mutableIntStateOf(0) }
  var checkoutPlan by remember { mutableStateOf<Pair<SubscriptionTier, BillingCycle>?>(null) }
  var selectedInvoice by remember { mutableStateOf<Invoice?>(null) }
  var showIntegrationInfo by remember { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          androidx.compose.foundation.layout.Row {
            Icon(Icons.Default.Shield, contentDescription = null, tint = IndigoPrimary)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(horizontal = 4.dp))
            androidx.compose.foundation.layout.Column {
              Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
              Text("Showcase prototype · no live billing", fontSize = 11.sp, color = CyanAccent)
            }
          }
        },
        actions = {
          IconButton(onClick = { showIntegrationInfo = true }, modifier = Modifier.testTag("topbar_settings_button")) {
            Icon(Icons.Default.Settings, contentDescription = "Integration status")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      )
    },
    bottomBar = {
      NavigationBar(containerColor = MaterialTheme.colorScheme.surface, contentColor = IndigoPrimary) {
        val tabs = listOf(
          "Dashboard" to Icons.Default.Dashboard,
          "Plans" to Icons.Default.CreditCard,
          "History" to Icons.AutoMirrored.Filled.ReceiptLong,
          "Privacy" to Icons.Default.Security,
          "Guide" to Icons.AutoMirrored.Filled.MenuBook,
        )
        tabs.forEachIndexed { index, (label, icon) ->
          NavigationBarItem(
            icon = { Icon(icon, contentDescription = label) },
            label = { Text(label) },
            selected = currentTab == index,
            onClick = { currentTab = index },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = IndigoPrimary,
              selectedTextColor = IndigoPrimary,
              indicatorColor = IndigoPrimary.copy(alpha = 0.15f),
            ),
            modifier = Modifier.testTag("nav_tab_${label.lowercase()}"),
          )
        }
      }
    },
  ) { innerPadding ->
    Box(Modifier.fillMaxSize().padding(innerPadding)) {
      when (currentTab) {
        0 -> DashboardScreen(
          billingManager = billingManager,
          onNavigateToPlans = { currentTab = 1 },
          onNavigateToHistory = { currentTab = 2 },
          onNavigateToSecurity = { currentTab = 3 },
          onViewInvoice = { selectedInvoice = it },
          onOpenStripeSettings = { showIntegrationInfo = true },
        )
        1 -> PlansBillingScreen(billingManager) { tier, cycle -> checkoutPlan = tier to cycle }
        2 -> BillingHistoryScreen(billingManager) { selectedInvoice = it }
        3 -> SecurityPrivacyScreen(billingManager)
        else -> UserGuideScreen()
      }
    }
  }

  checkoutPlan?.let { (tier, cycle) ->
    StripePaymentSheetDialog(plan = tier, cycle = cycle, onDismiss = { checkoutPlan = null })
  }
  selectedInvoice?.let { invoice ->
    InvoiceReceiptDialog(invoice = invoice, onDismiss = { selectedInvoice = null })
  }
  if (showIntegrationInfo) {
    StripeSettingsDialog(onDismiss = { showIntegrationInfo = false })
  }
}
