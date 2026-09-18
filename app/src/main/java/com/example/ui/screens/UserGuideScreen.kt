package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
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
        Text(
          text = "bash",
          color = Color(0xFF94A3B8),
          fontFamily = FontFamily.Monospace,
          fontSize = 11.sp
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = """
# 1. Clone repo & navigate into directory
git clone https://github.com/organization/billinghub.git
cd billinghub

# 2. Configure .env with your Stripe Publishable Key
echo "STRIPE_PUBLISHABLE_KEY=pk_test_..." >> .env

# 3. Compile and run unit tests
gradle :app:testDebugUnitTest

# 4. Build debug APK
gradle :app:assembleDebug
        """.trimIndent(),
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = Color(0xFF38BDF8),
        lineHeight = 17.sp
      )
    }
  }
}
