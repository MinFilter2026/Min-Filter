package com.minfilter.app.admin

import android.app.admin.DeviceAdminReceiver

/**
 * Device-admin receiver used for managed-device deployment.
 * Strong uninstall prevention is only available when Min Filter is provisioned
 * as a Device Owner by an administrator; normal installs cannot self-grant this role.
 */
class MinFilterDeviceAdminReceiver : DeviceAdminReceiver()
