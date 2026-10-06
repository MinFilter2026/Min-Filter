package com.minfilter.app.protection

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import com.minfilter.app.admin.MinFilterDeviceAdminReceiver
import com.minfilter.app.protection.LockExpiryReceiver
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * App-level protection PIN and time lock.
 *
 * The time lock prevents Min Filter protection/settings from being changed
 * through the app until the selected period expires. It is not a substitute
 * for Android Device Owner management, which is required for strong
 * uninstall prevention.
 */
class AdminLock(context: Context) {
    private val app = context.applicationContext
    private val p = app.getSharedPreferences("admin", Context.MODE_PRIVATE)

    companion object {
        val LOCK_DAYS = intArrayOf(1, 3, 7, 15, 30)
    }

    fun setPin(pin: String) {
        require(pin.length in 4..12 && pin.all(Char::isDigit)) { "PIN must be 4-12 digits" }
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        p.edit()
            .putString("salt", salt.toHex())
            .putString("hash", digest(salt, pin))
            .apply()
    }

    fun clearPin() = p.edit().remove("salt").remove("hash").apply()

    fun hasPin(): Boolean = p.getString("hash", null) != null && p.getString("salt", null) != null

    fun verify(pin: String): Boolean {
        val salt = p.getString("salt", null)?.hexToBytes() ?: return false
        val expected = p.getString("hash", null) ?: return false
        return MessageDigest.isEqual(digest(salt, pin).toByteArray(), expected.toByteArray())
    }

    fun startTimedLock(days: Int) {
        require(days in LOCK_DAYS) { "Unsupported lock duration" }
        val until = System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L
        p.edit().putLong("lock_until", until).putInt("lock_days", days).apply()
        scheduleExpiry(until)
    }

    fun clearTimedLock() {
        p.edit().remove("lock_until").remove("lock_days").apply()
        val alarm = app.getSystemService(AlarmManager::class.java)
        alarm.cancel(expiryPendingIntent())
    }


    private fun scheduleExpiry(until: Long) {
        val alarm = app.getSystemService(AlarmManager::class.java)
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, until, expiryPendingIntent())
    }

    private fun expiryPendingIntent(): PendingIntent {
        val intent = Intent(app, LockExpiryReceiver::class.java)
        return PendingIntent.getBroadcast(app, 1907, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun isTimedLocked(): Boolean {
        val until = p.getLong("lock_until", 0L)
        if (until <= 0L) {
            syncDeviceOwnerUninstallPolicy(false)
            return false
        }
        if (System.currentTimeMillis() >= until) {
            clearTimedLock()
            syncDeviceOwnerUninstallPolicy(false)
            return false
        }
        syncDeviceOwnerUninstallPolicy(true)
        return true
    }

    fun syncDeviceOwnerUninstallPolicy(block: Boolean = isTimedLockedRaw()) {
        if (!isDeviceOwner()) return
        runCatching {
            val dpm = app.getSystemService(DevicePolicyManager::class.java)
            val admin = ComponentName(app, MinFilterDeviceAdminReceiver::class.java)
            dpm.setUninstallBlocked(admin, app.packageName, block)
        }
    }

    private fun isTimedLockedRaw(): Boolean {
        val until = p.getLong("lock_until", 0L)
        return until > 0L && System.currentTimeMillis() < until
    }

    fun lockUntil(): Long = p.getLong("lock_until", 0L)
    fun lockDays(): Int = p.getInt("lock_days", 0)

    fun remainingMillis(): Long = (lockUntil() - System.currentTimeMillis()).coerceAtLeast(0L)

    fun isDeviceOwner(): Boolean {
        val dpm = app.getSystemService(DevicePolicyManager::class.java)
        return dpm.isDeviceOwnerApp(app.packageName)
    }

    fun isActiveDeviceAdmin(): Boolean {
        val dpm = app.getSystemService(DevicePolicyManager::class.java)
        val admin = ComponentName(app, MinFilterDeviceAdminReceiver::class.java)
        return dpm.isAdminActive(admin)
    }

    private fun digest(salt: ByteArray, pin: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(salt + pin.toByteArray(Charsets.UTF_8)).toHex()
    }

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }
    private fun String.hexToBytes() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
