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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.service.StripeBillingManager
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen

@Composable
fun SecurityPrivacyScreen(
  billingManager: StripeBillingManager,
) {
  val securityLogs by billingManager.securityLogs.collectAsState()
  val biometricLock by billingManager.biometricLockEnabled.collectAsState()
  val ccpaOptOut by billingManager.ccpaOptOut.collectAsState()
  val analyticsTracking by billingManager.analyticsTracking.collectAsState()

  var showExportDialog by remember { mutableStateOf(false) }
  var exportedJsonContent by remember { mutableStateOf("") }
  var showDeleteDialog by remember { mutableStateOf(false) }
  var deletionConfirmed by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("security_privacy_screen"),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Hero Banner with Generated Security Graphic
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
              painter = painterResource(id = R.drawable.img_security_privacy),
              contentDescription = "Enterprise Security Architecture",
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
                text = "Enterprise Security & Privacy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Hardware Keystore • Zero-Knowledge Tokenization • GDPR/CCPA",
                style = MaterialTheme.typography.bodySmall,
                color = CyanAccent
              )
            }
          }
        }
      }
    }

    // SECTION 1: Encryption Standards
    item {
      Text(
        text = "Cryptographic & Encryption Standards",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        EncryptionStandardCard(
          title = "Data at Rest: AES-256-GCM",
          subtitle = "Authenticated 256-bit Galois/Counter Mode",
          description = "All local database caches, billing credentials, and session tokens are encrypted using AES-256-GCM with randomized 96-bit IVs and 128-bit authentication tags, preventing ciphertext tampering.",
          badge = "FIPS 140-2"
        )
        EncryptionStandardCard(
          title = "Data in Transit: TLS 1.3 Strict",
          subtitle = "ECDHE Forward Secrecy & Pinning",
          description = "All network requests to Stripe API and billing backends require TLS 1.3 with Perfect Forward Secrecy (PFS). Public key pinning prevents man-in-the-middle (MITM) and rogue proxy inspection.",
          badge = "Zero-Trust"
        )
        EncryptionStandardCard(
          title = "Hardware Keystore: Android StrongBox",
          subtitle = "Hardware Security Module (HSM)",
          description = "Private asymmetric keys are generated inside the tamper-resistant Android Keystore StrongBox hardware enclave. Keys never enter application memory space and cannot be dumped via root/debuggers.",
          badge = "Hardware-Backed"
        )
        EncryptionStandardCard(
          title = "Stripe Payment Tokenization",
          subtitle = "PCI-DSS Level 1 Service Provider",
          description = "Cardholder data (PAN, CVV) is directly transmitted from client UI to Stripe's tokenization vault. The application server only receives ephemeral tokens (tok_*, pm_*), reducing PCI audit scope to SAQ-A.",
          badge = "PCI-DSS Level 1"
        )
      }
    }

    // SECTION 2: User Information Handling
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "User Information Handling & Compliance",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    item {
      ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Data Minimization & Retention Policy",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "We adhere to strict data minimization principles. We only collect the minimal billing metadata necessary to provision subscription services. IP addresses are truncated after 24 hours, and operational telemetry is stored in hashed pseudonymized format with 30-day automated rolling purges.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
          )

          Spacer(modifier = Modifier.height(14.dp))
          Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
          Spacer(modifier = Modifier.height(14.dp))

          // Privacy preference toggles
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Biometric Lock Protection",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Require fingerprint or device PIN to access billing details",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = biometricLock,
              onCheckedChange = { billingManager.toggleBiometricLock(it) },
              colors = SwitchDefaults.colors(checkedTrackColor = IndigoPrimary),
              modifier = Modifier.testTag("biometric_lock_switch")
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "CCPA Do Not Sell My Information",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Opt out of third-party analytics and data sharing",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = ccpaOptOut,
              onCheckedChange = { billingManager.toggleCcpaOptOut(it) },
              colors = SwitchDefaults.colors(checkedTrackColor = CyanAccent),
              modifier = Modifier.testTag("ccpa_switch")
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Anonymous Diagnostic Telemetry",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Send crash reports with zero-knowledge obfuscation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Switch(
              checked = analyticsTracking,
              onCheckedChange = { billingManager.toggleAnalyticsTracking(it) },
              colors = SwitchDefaults.colors(checkedTrackColor = IndigoPrimary),
              modifier = Modifier.testTag("telemetry_switch")
            )
          }
        }
      }
    }

    // SECTION 3: GDPR Rights Actions
    item {
      Text(
        text = "Your Privacy Rights (GDPR & CCPA)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = {
            exportedJsonContent = billingManager.exportUserDataJson()
            showExportDialog = true
          },
          modifier = Modifier
            .weight(1f)
            .testTag("export_data_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Export Data (JSON)", fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = { showDeleteDialog = true },
          modifier = Modifier
            .weight(1f)
            .testTag("delete_data_button"),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Right to Erasure", fontSize = 12.sp)
        }
      }
    }

    // SECTION 4: Live Cryptographic Audit Trail
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Cryptographic Audit Ledger",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "SHA-256 Verified",
          style = MaterialTheme.typography.labelSmall,
          fontFamily = FontFamily.Monospace,
          color = SuccessGreen
        )
      }
    }

    items(securityLogs) { log ->
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(
                    if (log.severity == "SUCCESS") SuccessGreen else if (log.severity == "WARN") Color(0xFFF59E0B) else CyanAccent,
                    CircleShape
                  )
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = log.eventType,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = log.timestamp,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = log.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = log.cryptographicProof,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = CyanAccent.copy(alpha = 0.8f)
          )
        }
      }
    }
  }

  // Export Dialog
  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = { Text("GDPR Article 20 Data Archive") },
      text = {
        Column {
          Text("Below is your structured machine-readable profile archive:")
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = exportedJsonContent,
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              modifier = Modifier.padding(10.dp),
              lineHeight = 14.sp
            )
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close")
        }
      }
    )
  }

  // Delete Request Dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text("Request Account & Data Erasure") },
      text = {
        Text("Under GDPR Article 17 (Right to be Forgotten), initiating this request will revoke your active subscription, invalidate all Stripe customer tokens, and permanently purge your billing records from the cluster after the statutory cooling period.")
      },
      confirmButton = {
        Button(
          onClick = {
            billingManager.requestAccountDeletion()
            showDeleteDialog = false
            deletionConfirmed = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
          Text("Confirm Hard Purge")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun EncryptionStandardCard(
  title: String,
  subtitle: String,
  description: String,
  badge: String,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CyanAccent.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f))
        ) {
          Text(
            text = badge,
            color = CyanAccent,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = IndigoPrimary,
        modifier = Modifier.padding(top = 2.dp)
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 18.sp
      )
    }
  }
}
