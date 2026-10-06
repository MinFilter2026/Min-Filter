#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

# Static checks that do not require Android SDK/network access.
test -f app/src/main/AndroidManifest.xml
test -f app/build.gradle.kts
test -f .github/workflows/build-apk.yml
test -f app/src/main/java/com/minfilter/app/MainActivity.kt
test -f app/src/main/java/com/minfilter/app/filter/MinFilterVpnService.kt
test -f app/src/main/java/com/minfilter/app/protection/AdminLock.kt
test -f app/src/main/java/com/minfilter/app/protection/LockExpiryReceiver.kt
test -f app/src/main/java/com/minfilter/app/admin/MinFilterDeviceAdminReceiver.kt

grep -q 'versionName="1.5.2"' app/build.gradle.kts
grep -q 'versionCode=9' app/build.gradle.kts
grep -q 'releases/download/v\$mihomoVersion/libmihomo-android-v\$mihomoVersion.aar' app/build.gradle.kts
grep -q 'LOCK_DAYS = intArrayOf(1, 3, 7, 15, 30)' app/src/main/java/com/minfilter/app/protection/AdminLock.kt
grep -q 'setUninstallBlocked' app/src/main/java/com/minfilter/app/protection/AdminLock.kt
grep -q 'FOREGROUND_SERVICE_SPECIAL_USE' app/src/main/AndroidManifest.xml
grep -q 'PROPERTY_SPECIAL_USE_FGS_SUBTYPE' app/src/main/AndroidManifest.xml
for lang in bn en ar ur hi; do grep -q '"'"$lang"'"' app/src/main/java/com/minfilter/app/MainActivity.kt; done

echo 'Min Filter v1.5.2 verification: PASS'
