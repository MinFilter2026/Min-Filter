# Build Min Filter on GitHub / phone

1. Create a GitHub repository and upload the contents of this folder (the folder containing `settings.gradle.kts`).
2. Push to the `main` branch.
3. Open **Actions → Build Min Filter APK**.
4. Wait for the workflow to finish.
5. Download the `MinFilter-debug-apk` artifact.

The workflow installs JDK 17 and Gradle 8.9. During the build it downloads the pinned libmihomo Android v0.3.5 AAR from its GitHub Release. Internet access is therefore required for the CI build.

## Before calling it a final release
Test on a real Android phone: VPN start/stop, normal browsing, blocked domains, trusted domains, Wi-Fi/mobile-data switching, screen-off behavior, reboot, and coexistence with another VPN.


### Protection Lock test (v1.5.2)
1. Open Settings → Protection Lock.
2. Create/enter the Protection PIN.
3. Select 1, 3, 7, 15 or 30 days.
4. Confirm that the main Protection control cannot be turned off during the lock.
5. Confirm Filter/Settings changes require the PIN and are blocked by the timed lock.
6. Confirm the remaining time is shown.

### Strong uninstall protection (managed test device only)
A normal Android installation cannot self-grant Device Owner. For a dedicated test device, provision Min Filter as Device Owner using Android's supported device-management provisioning flow, then verify Settings → Device Owner / Uninstall protection reports active. During a timed lock, the app uses Android's uninstall-block policy; at expiry the policy is released. Do not use device-owner provisioning on a primary phone unless you understand Android device-management implications.
