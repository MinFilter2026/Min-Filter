#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
need=(
  "$ROOT/app/src/main/AndroidManifest.xml"
  "$ROOT/app/src/main/java/com/minfilter/app/MainActivity.kt"
  "$ROOT/app/src/main/java/com/minfilter/app/filter/MinFilterVpnService.kt"
  "$ROOT/app/src/main/java/com/minfilter/app/data/BlockRules.kt"
  "$ROOT/app/src/main/java/com/minfilter/app/protection/AdminLock.kt"
  "$ROOT/app/src/main/java/com/minfilter/app/protection/LockExpiryReceiver.kt"
  "$ROOT/app/src/main/java/com/minfilter/app/admin/MinFilterDeviceAdminReceiver.kt"
  "$ROOT/app/src/main/java/com/minfilter/app/ui/Language.kt"
  "$ROOT/app/src/main/java/com/minfilter/app/ui/ContentLibrary.kt"
  "$ROOT/.github/workflows/build-apk.yml"
  "$ROOT/docs/index.html"
)
for f in "${need[@]}"; do test -f "$f" || { echo "MISSING: $f"; exit 1; }; done
grep -q 'versionName="1.5.2"' "$ROOT/app/build.gradle.kts"
grep -q 'versionCode=9' "$ROOT/app/build.gradle.kts"
grep -q 'family_dns' "$ROOT/app/src/main/java/com/minfilter/app/MainActivity.kt"
grep -q 'malware_dns' "$ROOT/app/src/main/java/com/minfilter/app/filter/MinFilterVpnService.kt"
grep -q 'class AdminLock' "$ROOT/app/src/main/java/com/minfilter/app/protection/AdminLock.kt"
for lang in bn en ar ur hi; do grep -q '"'$lang'"' "$ROOT/app/src/main/java/com/minfilter/app/MainActivity.kt"; done
grep -q 'ContentLibrary.items' "$ROOT/app/src/main/java/com/minfilter/app/MainActivity.kt"
echo 'Min Filter v1.5.2 static audit: PASS'

grep -q 'LOCK_DAYS = intArrayOf(1, 3, 7, 15, 30)' "$ROOT/app/src/main/java/com/minfilter/app/protection/AdminLock.kt"
grep -q 'setUninstallBlocked' "$ROOT/app/src/main/java/com/minfilter/app/MainActivity.kt"
grep -q 'LockExpiryReceiver' "$ROOT/app/src/main/AndroidManifest.xml"
grep -q 'FOREGROUND_SERVICE_SPECIAL_USE' "$ROOT/app/src/main/AndroidManifest.xml"
grep -q 'setUninstallBlocked' "$ROOT/app/src/main/java/com/minfilter/app/protection/AdminLock.kt"
test -f "$ROOT/THIRD_PARTY_NOTICES.md"
test -f "$ROOT/tools_verify.sh"
