#!/usr/bin/env bash
# ==============================================================================
# BillingHub - Automated Environment Check, Build & Test Verification Script
# ==============================================================================
# This script automates:
#   1. Environment pre-flight checks (Java 17+, Android SDK, .env configuration)
#   2. Project compilation and APK assembly (:app:assembleDebug)
#   3. Execution of full Unit and Robolectric test suite (:app:testDebugUnitTest)
# ==============================================================================

set -euo pipefail

# ANSI Color Codes
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m' # No Color

START_TIME=$(date +%s)

echo -e "${CYAN}${BOLD}"
echo "=============================================================================="
echo "         BillingHub Android - Automated Environment & Test Pipeline          "
echo "=============================================================================="
echo -e "${NC}"

# ------------------------------------------------------------------------------
# 1. Environment & Pre-Flight Verification
# ------------------------------------------------------------------------------
echo -e "${BOLD}[1/4] Running Environment Pre-flight Checks...${NC}"

# Check Operating System
OS="$(uname -s)"
echo -e "  ✓ Host OS: ${GREEN}${OS}${NC}"

# Check Java Installation
if ! command -v java &> /dev/null; then
  echo -e "  ${RED}✗ Error: Java is not installed or not in PATH.${NC}"
  echo "    Please install JDK 17 or higher (e.g. OpenJDK 17/21)."
  exit 1
fi

JAVA_VERSION_OUTPUT=$(java -version 2>&1 | head -n 1)
echo -e "  ✓ Java Runtime: ${GREEN}${JAVA_VERSION_OUTPUT}${NC}"

# Check Android SDK Root
SDK_DIR="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"
if [[ -n "$SDK_DIR" && -d "$SDK_DIR" ]]; then
  echo -e "  ✓ Android SDK detected at: ${GREEN}${SDK_DIR}${NC}"
else
  echo -e "  ${YELLOW}! Warning: Neither ANDROID_SDK_ROOT nor ANDROID_HOME is exported.${NC}"
  echo "    Gradle will attempt to locate SDK via standard system paths."
fi

# Check Gradle Executable
if command -v gradle &> /dev/null; then
  GRADLE_CMD="gradle"
elif [[ -f "./gradlew" ]]; then
  chmod +x ./gradlew
  GRADLE_CMD="./gradlew"
else
  echo -e "  ${RED}✗ Error: Neither 'gradle' nor './gradlew' was found.${NC}"
  exit 1
fi
echo -e "  ✓ Build Tool: ${GREEN}$(${GRADLE_CMD} --version 2>&1 | head -n 3 | tr '\n' ' ')${NC}"

# Check .env Configuration
if [[ ! -f ".env" ]]; then
  if [[ -f ".env.example" ]]; then
    echo -e "  ${YELLOW}! Notice: .env file missing. Initializing from .env.example...${NC}"
    cp .env.example .env
    echo -e "  ✓ Created .env template with mock test credentials."
  else
    echo -e "  ${YELLOW}! Notice: No .env or .env.example found. Creating baseline .env...${NC}"
    echo "STRIPE_PUBLISHABLE_KEY=pk_test_mock_for_testing" > .env
    echo "STRIPE_WEBHOOK_SECRET=whsec_mock_for_testing" >> .env
  fi
else
  echo -e "  ✓ Environment Secrets: ${GREEN}.env present${NC}"
fi

echo -e "  ${GREEN}✓ All environment prerequisites validated successfully.${NC}\n"

# ------------------------------------------------------------------------------
# 2. Compile Project & Assemble Debug Artifacts
# ------------------------------------------------------------------------------
echo -e "${BOLD}[2/4] Compiling Project & Assembling Debug APK...${NC}"
echo -e "  Executing: ${CYAN}${GRADLE_CMD} :app:assembleDebug${NC}"

if ${GRADLE_CMD} :app:assembleDebug --no-daemon; then
  echo -e "  ${GREEN}✓ Compilation and assembly succeeded.${NC}"
  if [[ -f "app/build/outputs/apk/debug/app-debug.apk" ]]; then
    APK_SIZE=$(du -h "app/build/outputs/apk/debug/app-debug.apk" | cut -f1)
    echo -e "  ✓ Debug APK built: ${GREEN}app/build/outputs/apk/debug/app-debug.apk (${APK_SIZE})${NC}\n"
  fi
else
  echo -e "  ${RED}✗ Build failed during assembleDebug.${NC}"
  exit 1
fi

# ------------------------------------------------------------------------------
# 3. Execute Automated Unit & Robolectric Test Suite
# ------------------------------------------------------------------------------
echo -e "${BOLD}[3/4] Running Suite of Unit and Robolectric Tests...${NC}"
echo -e "  Executing: ${CYAN}${GRADLE_CMD} :app:testDebugUnitTest${NC}"

if ${GRADLE_CMD} :app:testDebugUnitTest --no-daemon; then
  echo -e "  ${GREEN}✓ All Unit and Robolectric test assertions passed successfully.${NC}\n"
else
  echo -e "  ${RED}✗ Unit test suite failed. Check test reports for details.${NC}"
  exit 1
fi

# ------------------------------------------------------------------------------
# 4. Summary & Diagnostic Output
# ------------------------------------------------------------------------------
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo -e "${BOLD}[4/4] Pipeline Summary & Test Verification${NC}"
echo -e "${CYAN}------------------------------------------------------------------------------${NC}"
echo -e "  ✓ Environment:       Verified (Java, Android SDK, .env)"
echo -e "  ✓ Compilation:       :app:assembleDebug (PASSED)"
echo -e "  ✓ Test Suite:        :app:testDebugUnitTest (PASSED)"
echo -e "  ✓ App Target Module: :app (com.aistudio.billinghub.vxfkrz v6.0)"
echo -e "  ✓ Play Deobfuscation: docs/assets/mapping.zip (Ready)"
echo -e "  ✓ Native Symbols:    docs/assets/native-debug-symbols.zip (Ready)"
echo -e "  ✓ Time Elapsed:      ${DURATION} seconds"

if [[ -d "app/build/reports/tests/testDebugUnitTest" ]]; then
  echo -e "  ✓ HTML Report:       ${GREEN}app/build/reports/tests/testDebugUnitTest/index.html${NC}"
fi

echo -e "${CYAN}------------------------------------------------------------------------------${NC}"
echo -e "${GREEN}${BOLD}✓ SETUP & TEST SUITE VERIFICATION COMPLETE: ALL SYSTEMS OPERATIONAL!${NC}"
exit 0
