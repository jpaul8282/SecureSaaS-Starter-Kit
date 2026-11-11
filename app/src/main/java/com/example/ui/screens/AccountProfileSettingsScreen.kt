package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActiveSession
import com.example.model.UserProfile
import com.example.service.StripeBillingManager
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepNavyDark
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountProfileSettingsScreen(
  billingManager: StripeBillingManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val userProfile by billingManager.userProfile.collectAsState()
  val subscription by billingManager.subscription.collectAsState()
  val activeSessions by billingManager.activeSessions.collectAsState()

  // Form states for editing
  var fullName by remember(userProfile.fullName) { mutableStateOf(userProfile.fullName) }
  var email by remember(userProfile.email) { mutableStateOf(userProfile.email) }
  var companyName by remember(userProfile.companyName) { mutableStateOf(userProfile.companyName) }
  var phone by remember(userProfile.phone) { mutableStateOf(userProfile.phone) }
  var billingEmail by remember(userProfile.billingEmail) { mutableStateOf(userProfile.billingEmail) }
  var taxId by remember(userProfile.taxId) { mutableStateOf(userProfile.taxId) }
  var addressStreet by remember(userProfile.billingAddressStreet) { mutableStateOf(userProfile.billingAddressStreet) }
  var city by remember(userProfile.billingCity) { mutableStateOf(userProfile.billingCity) }
  var stateProvince by remember(userProfile.billingState) { mutableStateOf(userProfile.billingState) }
  var postalCode by remember(userProfile.billingPostalCode) { mutableStateOf(userProfile.billingPostalCode) }
  var country by remember(userProfile.billingCountry) { mutableStateOf(userProfile.billingCountry) }
  var currency by remember(userProfile.currencyPreference) { mutableStateOf(userProfile.currencyPreference) }

  var showDeleteDialog by remember { mutableStateOf(false) }
  var showResetDialog by remember { mutableStateOf(false) }
  var isSavedFeedbackVisible by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Account & Profile",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Personal info, billing entity & security",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("account_back_button")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back to previous screen"
            )
          }
        },
        actions = {
          IconButton(
            onClick = {
              showResetDialog = true
            },
            modifier = Modifier.testTag("account_reset_button")
          ) {
            Icon(
              Icons.Default.Refresh,
              contentDescription = "Reset profile defaults",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
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
        .testTag("account_profile_settings_screen"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // 1. Profile Hero Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("profile_hero_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = DeepNavyDark),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(80.dp)
                .background(
                  Brush.linearGradient(listOf(IndigoPrimary, CyanAccent)),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = userProfile.avatarInitials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
              )
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .size(24.dp)
                  .background(Color.White, CircleShape)
                  .padding(2.dp)
                  .background(IndigoPrimary, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  Icons.Default.Edit,
                  contentDescription = "Edit Avatar",
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = userProfile.fullName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 4.dp)
            ) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Verified Identity",
                tint = CyanAccent,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = userProfile.email,
                style = MaterialTheme.typography.bodySmall,
                color = CyanAccent
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x334F46E5)
              ) {
                Text(
                  text = userProfile.role,
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x3306B6D4)
              ) {
                Text(
                  text = "Stripe ID: ${subscription.stripeCustomerId}",
                  color = CyanAccent,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      // Save confirmation notification
      item {
        AnimatedVisibility(visible = isSavedFeedbackVisible) {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .widthIn(max = 680.dp),
            shape = RoundedCornerShape(12.dp),
            color = SuccessGreen.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = SuccessGreen
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Account and profile changes saved successfully!",
                color = SuccessGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
            }
          }
        }
      }

      // 2. Personal & Organization Details Section
      item {
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("card_personal_details"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = IndigoPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Personal & Organization Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            HorizontalDivider()

            OutlinedTextField(
              value = fullName,
              onValueChange = { fullName = it },
              label = { Text("Full Name") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_full_name"),
              singleLine = true
            )

            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Email Address") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              trailingIcon = {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = SuccessGreen.copy(alpha = 0.15f),
                  modifier = Modifier.padding(end = 8.dp)
                ) {
                  Text(
                    text = "VERIFIED",
                    color = SuccessGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_email"),
              singleLine = true
            )

            OutlinedTextField(
              value = companyName,
              onValueChange = { companyName = it },
              label = { Text("Company / Legal Entity Name") },
              leadingIcon = { Icon(Icons.Default.Apartment, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_company_name"),
              singleLine = true
            )

            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("Phone Number") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_phone"),
              singleLine = true
            )

            Button(
              onClick = {
                val updated = userProfile.copy(
                  fullName = fullName,
                  email = email,
                  companyName = companyName,
                  phone = phone,
                  avatarInitials = fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "AV" }
                )
                billingManager.updateUserProfile(updated)
                isSavedFeedbackVisible = true
                Toast.makeText(context, "Personal details updated", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("save_personal_details_button"),
              colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Save Personal Info", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 3. Stripe Billing & Tax Details
      item {
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("card_billing_tax"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                tint = IndigoPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Stripe Billing & Tax Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            Text(
              text = "This entity will appear on all generated Stripe receipts, tax statements, and invoices.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            OutlinedTextField(
              value = billingEmail,
              onValueChange = { billingEmail = it },
              label = { Text("Invoice Recipient Email") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_billing_email"),
              singleLine = true
            )

            OutlinedTextField(
              value = taxId,
              onValueChange = { taxId = it },
              label = { Text("Tax ID / EIN / VAT Number") },
              leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_tax_id"),
              singleLine = true
            )

            OutlinedTextField(
              value = addressStreet,
              onValueChange = { addressStreet = it },
              label = { Text("Billing Street Address") },
              leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_address_street"),
              singleLine = true
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier
                  .weight(1f)
                  .testTag("input_city"),
                singleLine = true
              )
              OutlinedTextField(
                value = stateProvince,
                onValueChange = { stateProvince = it },
                label = { Text("State") },
                modifier = Modifier
                  .weight(0.6f)
                  .testTag("input_state"),
                singleLine = true
              )
              OutlinedTextField(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text("Postal") },
                modifier = Modifier
                  .weight(0.8f)
                  .testTag("input_postal"),
                singleLine = true
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedTextField(
                value = country,
                onValueChange = { country = it },
                label = { Text("Country") },
                modifier = Modifier
                  .weight(1f)
                  .testTag("input_country"),
                singleLine = true
              )
              OutlinedTextField(
                value = currency,
                onValueChange = { currency = it },
                label = { Text("Currency") },
                modifier = Modifier
                  .weight(0.8f)
                  .testTag("input_currency"),
                singleLine = true
              )
            }

            // Current Payment Method Summary
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.CreditCard,
                  contentDescription = null,
                  tint = IndigoPrimary,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = subscription.paymentMethodSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = "Default method for recurring subscription",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Button(
              onClick = {
                val updated = userProfile.copy(
                  billingEmail = billingEmail,
                  taxId = taxId,
                  billingAddressStreet = addressStreet,
                  billingCity = city,
                  billingState = stateProvince,
                  billingPostalCode = postalCode,
                  billingCountry = country,
                  currencyPreference = currency
                )
                billingManager.updateUserProfile(updated)
                isSavedFeedbackVisible = true
                Toast.makeText(context, "Billing address & tax profile saved", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("save_billing_tax_button"),
              colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Save Billing & Tax Profile", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 4. Security & Authentication Settings
      item {
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("card_security_settings"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = IndigoPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Security & Access Control",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            HorizontalDivider()

            // 2FA Toggle
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("toggle_2fa_row"),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Two-Factor Authentication (2FA)",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  text = "Require TOTP authenticator app verification on login",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Switch(
                checked = userProfile.twoFactorEnabled,
                onCheckedChange = { billingManager.toggleTwoFactor(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary),
                modifier = Modifier.testTag("switch_2fa")
              )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Biometric App Lock Toggle
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("toggle_biometric_row"),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Biometric App Unlock",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  text = "Prompt fingerprint or face unlock when opening BillingHub",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Switch(
                checked = userProfile.biometricAuthEnabled,
                onCheckedChange = { billingManager.toggleBiometricAppLock(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary),
                modifier = Modifier.testTag("switch_biometrics")
              )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Active Sessions
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Active Connected Sessions",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  text = "${activeSessions.size} recognized authorized devices",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              TextButton(
                onClick = {
                  billingManager.revokeOtherSessions()
                  Toast.makeText(context, "All remote sessions revoked", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.testTag("revoke_sessions_button")
              ) {
                Text("Revoke Others", color = ErrorRed, fontWeight = FontWeight.Bold)
              }
            }

            activeSessions.forEach { session ->
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.Devices,
                    contentDescription = null,
                    tint = if (session.isCurrentDevice) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = session.deviceName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                      )
                      if (session.isCurrentDevice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                          shape = RoundedCornerShape(4.dp),
                          color = SuccessGreen.copy(alpha = 0.15f)
                        ) {
                          Text(
                            text = "THIS DEVICE",
                            color = SuccessGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                    Text(
                      text = "${session.location} • ${session.lastActive}",
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
      }

      // 5. Billing & Notifications Preferences
      item {
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("card_notification_preferences"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                tint = IndigoPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Notifications & Invoice Alerts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }

            HorizontalDivider()

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Email PDF Invoice Receipts",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  text = "Send PDF receipt immediately upon Stripe charge",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Switch(
                checked = userProfile.emailInvoiceReceipts,
                onCheckedChange = { billingManager.toggleEmailInvoiceReceipts(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary),
                modifier = Modifier.testTag("switch_email_receipts")
              )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Payment Failure Realtime Alerts",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  text = "Urgent alerts when card expires or payment fails",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Switch(
                checked = userProfile.paymentFailedAlerts,
                onCheckedChange = { billingManager.togglePaymentAlerts(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary),
                modifier = Modifier.testTag("switch_payment_alerts")
              )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "API Usage Threshold Warning (80% & 95%)",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  text = "Notify when API request count nears tier limit",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Switch(
                checked = userProfile.usageThresholdAlerts,
                onCheckedChange = { billingManager.toggleUsageAlerts(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary),
                modifier = Modifier.testTag("switch_usage_alerts")
              )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Subscription Renewal Reminders",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Text(
                  text = "Notice 7 days before annual or monthly renewal",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Switch(
                checked = userProfile.renewalReminders,
                onCheckedChange = { billingManager.toggleRenewalReminders(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary),
                modifier = Modifier.testTag("switch_renewal_reminders")
              )
            }
          }
        }
      }

      // 6. Danger Zone & Account Operations
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
            .testTag("card_danger_zone"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
          border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f))
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Data Management & Account Zone",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ErrorRed
              )
            }

            Text(
              text = "Export your full structured profile archive under GDPR Article 20, or request zero-knowledge account erasure.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = ErrorRed.copy(alpha = 0.2f))

            OutlinedButton(
              onClick = {
                val json = billingManager.exportUserDataJson()
                Toast.makeText(context, "Exported profile archive (${json.length} bytes)", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("export_account_data_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Export Account Profile Archive (JSON)")
            }

            OutlinedButton(
              onClick = { showResetDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_profile_dialog_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Reset Profile to Default Settings")
            }

            Button(
              onClick = { showDeleteDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("delete_account_button"),
              colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Delete Account & Organization Data", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(30.dp))
      }
    }
  }

  // Delete Confirmation Dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed) },
      title = { Text("Delete Organization Account?") },
      text = {
        Text("This will immediately cancel your active ${subscription.tier.title} subscription, delete all Stripe customer linkages, and trigger a zero-knowledge purge of personal data.")
      },
      confirmButton = {
        Button(
          onClick = {
            billingManager.requestAccountDeletion()
            showDeleteDialog = false
            Toast.makeText(context, "Account deletion pipeline initiated", Toast.LENGTH_LONG).show()
            onBack()
          },
          colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
          Text("Confirm Erasure")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Reset Confirmation Dialog
  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      icon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = IndigoPrimary) },
      title = { Text("Reset Profile to Defaults?") },
      text = {
        Text("This will restore default organization settings, contact info, and notification preferences.")
      },
      confirmButton = {
        Button(
          onClick = {
            billingManager.resetProfileToDefaults()
            showResetDialog = false
            Toast.makeText(context, "Profile reset to defaults", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
        ) {
          Text("Reset Defaults")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
