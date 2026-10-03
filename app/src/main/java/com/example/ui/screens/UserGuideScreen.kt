package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SuccessGreen

@Composable
fun UserGuideScreen() {
  var selectedTab by remember { mutableIntStateOf(0) }
  val guideTabs = listOf("Dashboard", "Stripe Checkout", "Security", "Setup & Deploy")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("user_guide_screen")
  ) {
    // Top Tabs
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = IndigoPrimary,
      indicator = { tabPositions ->
        TabRowDefaults.SecondaryIndicator(
          Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
          color = CyanAccent,
          height = 3.dp
        )
      }
    ) {
      guideTabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Text(
              text = title,
              fontSize = 12.sp,
              fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
              maxLines = 1
            )
          }
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      when (selectedTab) {
        0 -> {
          // Guide Piece 1: Dashboard Guide
          item {
            GuideSectionCard(
              title = "1. Dashboard Navigation & Usage Guide",
              subtitle = "Master subscription health, billing quotas, and account analytics",
              imageRes = R.drawable.img_dashboard_guide,
              contentDescription = "Dashboard Metrics Guide Illustration",
              badgeText = "Part 1 of 4",
              bullets = listOf(
                "Subscription Status Header: Instant visibility into your active plan, current renewal date, and Stripe customer token.",
                "Real-Time Quota Meters: Live progress trackers for monthly API requests (e.g. 42.8k / 150k) and team seat allocations.",
                "Automated Invoicing: Past invoices with 1-click official Stripe receipts and clear payment timestamps.",
                "Stripe Webhook Telemetry: Monitor delivered webhooks with HMAC-SHA256 signature verification badges."
              )
            )
          }
        }
        1 -> {
          // Guide Piece 2: Stripe Billing Guide
          item {
            GuideSectionCard(
              title = "2. Stripe Payment & Subscription Checkout",
              subtitle = "How seamless card tokenization and tier switching work",
              imageRes = R.drawable.img_stripe_billing,
              contentDescription = "Stripe Payment Sheet Guide",
              badgeText = "Part 2 of 4",
              bullets = listOf(
                "Dynamic Plan Selection: Toggle between Monthly and Annual billing (with 20% annual discount) across Starter, Pro, and Enterprise.",
                "Stripe Elements Sheet: Client-side card input with Luhn algorithm validation, brand detection (Visa, MC, Amex), and instant 1-tap demo filler.",
                "Strong Customer Authentication (SCA): Complies with European PSD2 3D Secure 2 protocols for seamless fraud mitigation.",
                "Zero-Knowledge Tokenization: Credit card PAN and CVC never hit application servers; they are exchanged directly for Stripe payment tokens."
              )
            )
          }
        }
        2 -> {
          // Guide Piece 3: Security & Encryption Guide
          item {
            GuideSectionCard(
              title = "3. Security Architecture & Encryption Standards",
              subtitle = "Hardware-backed keys, AES-256-GCM, and GDPR/CCPA enforcement",
              imageRes = R.drawable.img_security_privacy,
              contentDescription = "Enterprise Security Infographic",
              badgeText = "Part 3 of 4",
              bullets = listOf(
                "AES-256-GCM Encryption: Authenticated cipher with 96-bit initialization vectors and 128-bit authentication tags for all cached secrets.",
                "TLS 1.3 Strict Transport: Cryptographic forward secrecy and certificate pinning for all remote API communications.",
                "Android Keystore HSM: Hardware-isolated StrongBox Keymaster storing device private keys securely outside Android OS reach.",
                "GDPR Rights Enforcement: Interactive tools for Article 20 JSON data portability and Article 17 hard-purge Right to Erasure."
              )
            )
          }
        }
        3 -> {
          // Guide Piece 4: Setup & Instructions
          item {
            GuideSectionCard(
              title = "4. Setup Instructions & Deployment Pipeline",
              subtitle = "Build instructions, environment variables, and GitHub Pages hosting",
              imageRes = R.drawable.img_setup_guide,
              contentDescription = "Developer Setup and Deployment Workflow",
              badgeText = "Part 4 of 4",
              bullets = listOf(
                "Environment Configuration: Configure STRIPE_PUBLISHABLE_KEY and STRIPE_WEBHOOK_SECRET in /.env and the Secrets panel.",
                "Local Build: Run 'gradle assembleDebug' to produce the APK, verified via 'gradle testDebugUnitTest'.",
                "GitHub Pages Documentation: Hosted docs in /docs folder with static single-page web app and automated GitHub Actions workflow.",
                "Stripe Sandbox Testing: Test with card 4242 4242 4242 4242 to trigger successful subscription lifecycle events."
              )
            )
          }

          item {
            SetupCodeSnippetCard()
          }

          item {
            DeveloperSetupFaqCard()
          }

          item {
            val context = LocalContext.current
            ElevatedCard(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Need Developer Assistance?",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Having trouble configuring Stripe keys or webhook signatures? Send a pre-formatted diagnostics report to developer support.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                  onClick = {
                    val emailSubject = "BillingHub Developer Assistance Request"
                    val emailBody = """
--- Developer Assistance Request ---
App: BillingHub Android (v2.0, build 2)
Target SDK: 36 (Android 14+)
Stripe Integration Mode: Sandbox / Test
User Email: westerveldjp@gmail.com

--- Issue Details ---
Category: [Integration / Webhooks / Security / Billing]
Description: [Please describe your issue or question here]

--- Diagnostic Information ---
Device / Emulator Model: 
OS Version: 
Log / Error Trace: 
                    """.trimIndent()

                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                      data = Uri.parse("mailto:support@billinghub.io")
                      putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                      putExtra(Intent.EXTRA_TEXT, emailBody)
                    }
                    try {
                      context.startActivity(emailIntent)
                    } catch (e: Exception) {
                      // Handled
                    }
                  },
                  modifier = Modifier.fillMaxWidth(),
                  colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Contact Support", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun GuideSectionCard(
  title: String,
  subtitle: String,
  imageRes: Int,
  contentDescription: String,
  badgeText: String,
  bullets: List<String>,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
      ) {
        Image(
          painter = painterResource(id = imageRes),
          contentDescription = contentDescription,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
        Surface(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(12.dp),
          shape = RoundedCornerShape(10.dp),
          color = Color(0xCC0A0F1D)
        ) {
          Text(
            text = badgeText,
            color = CyanAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Column(modifier = Modifier.padding(18.dp)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = CyanAccent,
          modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          bullets.forEach { bullet ->
            Row(verticalAlignment = Alignment.Top) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = IndigoPrimary,
                modifier = Modifier
                  .size(18.dp)
                  .padding(top = 2.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = bullet,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 19.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SetupCodeSnippetCard() {
  val clipboardManager = LocalClipboardManager.current
  var isCopied by remember { mutableStateOf(false) }

  val snippetText = """
# Option A: Automated One-Step Verification Script
chmod +x test-setup.sh && ./test-setup.sh

# Option B: Manual Step-by-Step Setup
# 1. Clone repo & navigate into directory
git clone https://github.com/organization/billinghub.git
cd billinghub

# 2. Configure .env with your Stripe Publishable Key
echo "STRIPE_PUBLISHABLE_KEY=pk_test_..." >> .env

# 3. Compile and run unit tests
gradle :app:testDebugUnitTest

# 4. Build debug APK
gradle :app:assembleDebug
  """.trimIndent()

  LaunchedEffect(isCopied) {
    if (isCopied) {
      delay(2000)
      isCopied = false
    }
  }

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = Color(0xFF0F172A),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Terminal, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Terminal Quick Start",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "bash",
            color = Color(0xFF94A3B8),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isCopied) SuccessGreen.copy(alpha = 0.2f) else Color(0xFF1E293B),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isCopied) SuccessGreen.copy(alpha = 0.6f) else Color(0xFF475569)
            ),
            modifier = Modifier
              .clickable {
                clipboardManager.setText(AnnotatedString(snippetText))
                isCopied = true
              }
              .testTag("btn_copy_terminal_snippet")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                contentDescription = "Copy code",
                tint = if (isCopied) SuccessGreen else Color(0xFFE2E8F0),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isCopied) "Copied" else "Copy",
                color = if (isCopied) SuccessGreen else Color(0xFFE2E8F0),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = snippetText,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = Color(0xFF38BDF8),
        lineHeight = 17.sp
      )
    }
  }
}

private data class FaqItemData(
  val id: String,
  val category: String,
  val question: String,
  val answerSummary: String,
  val bulletPoints: List<String> = emptyList(),
  val codeSnippet: String? = null
)

@Composable
private fun DeveloperSetupFaqCard() {
  var selectedCategory by remember { mutableStateOf("All") }
  var expandedIds by remember { mutableStateOf(setOf("stripe_live_mode", "sec_aes_gcm")) }

  val faqList = remember {
    listOf(
      FaqItemData(
        id = "stripe_live_mode",
        category = "Stripe",
        question = "How do I switch from Stripe Sandbox test mode to Live mode?",
        answerSummary = "To transition to live payments, update your credentials in /.env (or AI Studio Secrets) and rebuild:",
        bulletPoints = listOf(
          "Update STRIPE_PUBLISHABLE_KEY: Replace 'pk_test_...' with your verified 'pk_live_...' key.",
          "Update STRIPE_WEBHOOK_SECRET: Configure your production endpoint secret 'whsec_...' from the Stripe Dashboard.",
          "Map Live Price IDs: Ensure plan enum IDs match the active recurring Price objects in Stripe Live mode."
        ),
        codeSnippet = "STRIPE_PUBLISHABLE_KEY=pk_live_51M...\nSTRIPE_WEBHOOK_SECRET=whsec_live_..."
      ),
      FaqItemData(
        id = "stripe_webhook_hmac",
        category = "Stripe",
        question = "How are Stripe webhook signatures verified & replay attacks stopped?",
        answerSummary = "Inbound webhook notifications are validated using constant-time HMAC-SHA256 signatures:",
        bulletPoints = listOf(
          "Signature Extraction: Parses 'Stripe-Signature' header to read unix timestamp (t) and signature (v1).",
          "HMAC-SHA256 Computation: Builds 'timestamp.rawPayload' and hashes using STRIPE_WEBHOOK_SECRET.",
          "Replay Attack Window: Payloads older than 300 seconds are rejected to eliminate replay risks.",
          "Side-Channel Immunity: Compared with MessageDigest.isEqual for constant-time cryptographic verification."
        )
      ),
      FaqItemData(
        id = "stripe_zero_knowledge",
        category = "Stripe",
        question = "Are credit card numbers (PAN / CVC) ever stored on the Android device?",
        answerSummary = "No. Card credentials never enter device flash, RAM, or Room SQLite databases:",
        bulletPoints = listOf(
          "Zero-Knowledge Tokenization: Card data is sent directly from isolated Stripe UI fields to Stripe PCI vaults.",
          "Non-Sensitive Tokens: The mobile app only receives a PaymentMethod token ('pm_...') and masked last 4 digits.",
          "PCI-DSS Scoping: Limits mobile application PCI compliance obligations strictly to SAQ A-EP."
        )
      ),
      FaqItemData(
        id = "sec_aes_gcm",
        category = "Security",
        question = "How does AES-256-GCM protect cached data and tokens at rest?",
        answerSummary = "NIST-standard Galois/Counter Mode authenticated encryption secures local records:",
        bulletPoints = listOf(
          "Authenticated Cipher: 128-bit authentication tag detects any unauthorized tampering or bit flipping.",
          "Unique Nonce: Generates a cryptographically strong 96-bit IV via SecureRandom for every write.",
          "Master Key Protection: Symmetric record encryption keys are wrapped by the master hardware key."
        )
      ),
      FaqItemData(
        id = "sec_keystore_hsm",
        category = "Security",
        question = "What is the Android Keystore StrongBox HSM architecture?",
        answerSummary = "Cryptographic keys reside inside dedicated, hardware-isolated security modules:",
        bulletPoints = listOf(
          "Dedicated Hardware Enclave: StrongBox operates on a separate microprocessor with dedicated CPU and RAM.",
          "Root & Probe Resilient: Private keys cannot be extracted via memory dumping, JTAG debugging, or OS rooting.",
          "Hardware Attestation: Generates X.509 certificate chains signed by Google and device OEMs."
        )
      ),
      FaqItemData(
        id = "sec_gdpr_compliance",
        category = "Security",
        question = "How do GDPR Article 17 (Erasure) and Article 20 (Portability) work?",
        answerSummary = "International privacy rights are operationalized directly through built-in app controls:",
        bulletPoints = listOf(
          "Data Portability (Art. 20): One-tap JSON export downloads full profile, active tier, and billing ledger.",
          "Right to Erasure (Art. 17): Irreversibly purges database records, wipes Keystore keys, and cancels subscriptions.",
          "CCPA Do-Not-Sell: Immediate toggle switch to opt out of third-party telemetry."
        )
      ),
      FaqItemData(
        id = "billing_history_pdf_receipts",
        category = "Stripe",
        question = "How do I access the 12-month billing history table and download PDF receipts?",
        answerSummary = "The dedicated 'History' screen provides an itemized table of your last 12 months of Stripe invoices:",
        bulletPoints = listOf(
          "Table Format: Displays Date, Invoice Number, Plan Tier, Amount, and Clearance Status.",
          "PDF Receipt Generator: Built with native Android Canvas & PdfDocument; downloads standard A4 receipts with cryptographic proof.",
          "Offline Storage & Share: PDF receipts are saved directly to Downloads and can be opened or shared with accounting software.",
          "Live Sync: Tap 'Sync Stripe' to refresh latest payments with realtime webhook verification."
        )
      ),
      FaqItemData(
        id = "play_deobfuscation_mapping",
        category = "Play Store",
        question = "Where do I upload the deobfuscation file (mapping.txt) in Google Play Console?",
        answerSummary = "Google Play Console requires the R8 mapping file to symbolicate obfuscated crash reports and ANRs:",
        bulletPoints = listOf(
          "Open Play Console: Navigate to Release > App bundle explorer.",
          "Select Version: Choose VersionCode 6 (Version 6.0) from the bundle selector.",
          "Downloads Tab: Click Downloads > Assets > Deobfuscation file and upload mapping.zip (or mapping.txt).",
          "ANR Resolution: Once uploaded, Android Vitals translates obfuscated stack traces (e.g. a.b.c) into human-readable Kotlin code lines."
        )
      ),
      FaqItemData(
        id = "play_native_debug_symbols",
        category = "Play Store",
        question = "How do I resolve the 'App Bundle contains native code, and you've not uploaded debug symbols' warning?",
        answerSummary = "Upload native-debug-symbols.zip containing unstripped .so debug symbols for all architectures:",
        bulletPoints = listOf(
          "Included Architectures: Contains symbols for arm64-v8a, armeabi-v7a, x86, and x86_64 for libandroidx.graphics.path.so and libdatastore_shared_counter.so.",
          "Upload Location: Play Console > App bundle explorer > Select version > Downloads tab > Native debug symbols.",
          "Native Tombstones: Allows Google Play to resolve native C/C++ crash tombstones and SIGSEGV stack traces."
        )
      )
    )
  }

  val filteredFaqs = remember(selectedCategory) {
    when (selectedCategory) {
      "Stripe" -> faqList.filter { it.category == "Stripe" }
      "Security" -> faqList.filter { it.category == "Security" }
      "Play Store" -> faqList.filter { it.category == "Play Store" }
      else -> faqList
    }
  }

  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("faq_section_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = CyanAccent.copy(alpha = 0.15f),
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "Frequently Asked Questions",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Common Stripe integration & security configuration queries",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Category filter chips
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        listOf("All", "Stripe", "Security", "Play Store").forEach { cat ->
          val isSelected = selectedCategory == cat
          val chipThemeColor = when (cat) {
            "Security" -> CyanAccent
            "Play Store" -> Color(0xFF10B981)
            else -> IndigoPrimary
          }
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = {
              Text(
                text = if (cat == "All") "All FAQs" else "$cat Queries",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = chipThemeColor.copy(alpha = 0.2f),
              selectedLabelColor = chipThemeColor
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
              selectedBorderColor = chipThemeColor
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // List of FAQ expandable items
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        filteredFaqs.forEach { faq ->
          val isExpanded = expandedIds.contains(faq.id)
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("faq_item_${faq.id}"),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isExpanded) {
                if (faq.category == "Stripe") IndigoPrimary.copy(alpha = 0.6f) else CyanAccent.copy(alpha = 0.6f)
              } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
              }
            )
          ) {
            Column(
              modifier = Modifier
                .clickable {
                  expandedIds = if (isExpanded) {
                    expandedIds - faq.id
                  } else {
                    expandedIds + faq.id
                  }
                }
                .padding(14.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  val catBadgeColor = when (faq.category) {
                    "Security" -> CyanAccent
                    "Play Store" -> Color(0xFF10B981)
                    else -> IndigoPrimary
                  }
                  val catBadgeIcon = when (faq.category) {
                    "Security" -> Icons.Default.Security
                    "Play Store" -> Icons.Default.Build
                    else -> Icons.Default.Payment
                  }
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = catBadgeColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 10.dp)
                  ) {
                    Icon(
                      catBadgeIcon,
                      contentDescription = null,
                      tint = catBadgeColor,
                      modifier = Modifier
                        .padding(5.dp)
                        .size(16.dp)
                    )
                  }
                  Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 19.sp
                  )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                  if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                  contentDescription = if (isExpanded) "Collapse FAQ" else "Expand FAQ",
                  tint = if (isExpanded) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(20.dp)
                )
              }

              AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
              ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                  HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    modifier = Modifier.padding(bottom = 10.dp)
                  )

                  Text(
                    text = faq.answerSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                  )

                  if (faq.bulletPoints.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                      faq.bulletPoints.forEach { point ->
                        Row(verticalAlignment = Alignment.Top) {
                          Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (faq.category == "Stripe") IndigoPrimary else CyanAccent,
                            modifier = Modifier
                              .padding(top = 2.dp, end = 8.dp)
                              .size(14.dp)
                          )
                          Text(
                            text = point,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                          )
                        }
                      }
                    }
                  }

                  if (faq.codeSnippet != null) {
                    val clipboardManager = LocalClipboardManager.current
                    var snippetCopied by remember { mutableStateOf(false) }

                    LaunchedEffect(snippetCopied) {
                      if (snippetCopied) {
                        delay(2000)
                        snippetCopied = false
                      }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                      modifier = Modifier.fillMaxWidth(),
                      shape = RoundedCornerShape(8.dp),
                      color = Color(0xFF0A0F1D),
                      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                      Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = "CONFIG",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.5.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                          )

                          Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (snippetCopied) SuccessGreen.copy(alpha = 0.2f) else Color(0xFF1E293B),
                            modifier = Modifier
                              .clickable {
                                clipboardManager.setText(AnnotatedString(faq.codeSnippet))
                                snippetCopied = true
                              }
                              .testTag("btn_copy_faq_${faq.id}")
                          ) {
                            Row(
                              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                              verticalAlignment = Alignment.CenterVertically
                            ) {
                              Icon(
                                if (snippetCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy code",
                                tint = if (snippetCopied) SuccessGreen else Color(0xFF94A3B8),
                                modifier = Modifier.size(11.dp)
                              )
                              Spacer(modifier = Modifier.width(3.dp))
                              Text(
                                text = if (snippetCopied) "Copied" else "Copy",
                                color = if (snippetCopied) SuccessGreen else Color(0xFFE2E8F0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                              )
                            }
                          }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                          text = faq.codeSnippet,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 10.5.sp,
                          color = Color(0xFF38BDF8),
                          lineHeight = 15.sp
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
