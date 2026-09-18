package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PlansBillingScreen
import com.example.ui.screens.SecurityPrivacyScreen
import com.example.ui.screens.UserGuideScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        BillingHubApp()
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingHubApp() {
  val billingManager = remember { StripeBillingManager() }
  val currentKey by billingManager.stripePublishableKey.collectAsState()
  var currentTab by remember { mutableIntStateOf(0) }

  // Dialog states
  var checkoutPlan by remember { mutableStateOf<Pair<SubscriptionTier, BillingCycle>?>(null) }
  var selectedInvoice by remember { mutableStateOf<Invoice?>(null) }
  var showStripeSettings by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(IndigoPrimary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(10.dp))
            androidx.compose.foundation.layout.Column {
              Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Stripe Billing & Security Hub",
                style = MaterialTheme.typography.bodySmall,
                color = CyanAccent,
                fontSize = 11.sp
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { showStripeSettings = true },
            modifier = Modifier.testTag("topbar_settings_button")
          ) {
            Icon(
              Icons.Default.Settings,
              contentDescription = "Stripe & Webhook Settings",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = IndigoPrimary,
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
          label = { Text("Dashboard") },
          selected = currentTab == 0,
          onClick = { currentTab = 0 },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = IndigoPrimary,
            selectedTextColor = IndigoPrimary,
            indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
          ),
          modifier = Modifier.testTag("nav_tab_dashboard")
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.CreditCard, contentDescription = "Plans & Billing") },
          label = { Text("Plans") },
          selected = currentTab == 1,
          onClick = { currentTab = 1 },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = IndigoPrimary,
            selectedTextColor = IndigoPrimary,
            indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
          ),
          modifier = Modifier.testTag("nav_tab_plans")
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Security, contentDescription = "Security & Privacy") },
          label = { Text("Security") },
          selected = currentTab == 2,
          onClick = { currentTab = 2 },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = IndigoPrimary,
            selectedTextColor = IndigoPrimary,
            indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
          ),
          modifier = Modifier.testTag("nav_tab_security")
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.MenuBook, contentDescription = "Users Guide") },
          label = { Text("Guide") },
          selected = currentTab == 3,
          onClick = { currentTab = 3 },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = IndigoPrimary,
            selectedTextColor = IndigoPrimary,
            indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
          ),
          modifier = Modifier.testTag("nav_tab_guide")
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        0 -> DashboardScreen(
          billingManager = billingManager,
          onNavigateToPlans = { currentTab = 1 },
          onNavigateToSecurity = { currentTab = 2 },
          onViewInvoice = { selectedInvoice = it },
          onOpenStripeSettings = { showStripeSettings = true }
        )
        1 -> PlansBillingScreen(
          billingManager = billingManager,
          onSelectPlanForCheckout = { tier, cycle ->
            checkoutPlan = Pair(tier, cycle)
          }
        )
        2 -> SecurityPrivacyScreen(
          billingManager = billingManager
        )
        3 -> UserGuideScreen()
      }
    }
  }

  // Stripe Checkout Dialog
  checkoutPlan?.let { (tier, cycle) ->
    StripePaymentSheetDialog(
      plan = tier,
      cycle = cycle,
      onDismiss = { checkoutPlan = null },
      onSuccess = { plan, billingCycle, card, expiry, cvc, name ->
        scope.launch {
          billingManager.processSubscriptionChange(plan, billingCycle, card, expiry, cvc, name)
          snackbarHostState.showSnackbar("Subscription upgraded to ${plan.title} successfully!")
        }
      }
    )
  }

  // Invoice Receipt Dialog
  selectedInvoice?.let { invoice ->
    InvoiceReceiptDialog(
      invoice = invoice,
      onDismiss = { selectedInvoice = null }
    )
  }

  // Stripe Settings Dialog
  if (showStripeSettings) {
    StripeSettingsDialog(
      currentKey = currentKey,
      onDismiss = { showStripeSettings = false },
      onSaveKey = { newKey ->
        billingManager.updateStripeKey(newKey)
        scope.launch {
          snackbarHostState.showSnackbar("Stripe Publishable Key updated.")
        }
      },
      onTriggerWebhook = { eventType ->
        billingManager.simulateWebhookDelivery(eventType)
        scope.launch {
          snackbarHostState.showSnackbar("Simulated webhook: $eventType received.")
        }
      }
    )
  }
}
