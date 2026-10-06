package com.minfilter.app.protection

import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import com.minfilter.app.admin.MinFilterDeviceAdminReceiver

class LockExpiryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: android.content.Intent?) {
        val lock = AdminLock(context)
        lock.clearTimedLock()
        if (lock.isDeviceOwner()) {
            runCatching {
                val dpm = context.getSystemService(DevicePolicyManager::class.java)
                val admin = ComponentName(context, MinFilterDeviceAdminReceiver::class.java)
                dpm.setUninstallBlocked(admin, context.packageName, false)
            }
        }
    }
}
