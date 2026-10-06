package com.minfilter.app.filter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import com.minfilter.app.R
import com.minfilter.app.data.BlockRules
import io.github.oviron.libmihomo.Clash
import io.github.oviron.libmihomo.TunInterface
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Full-device VPN bridge for Min Filter.
 *
 * The Android VpnService owns the TUN interface. The mihomo userspace network
 * stack reads/writes that TUN and handles TCP + UDP forwarding, DNS interception,
 * connection tracking and rule evaluation. Outbound sockets created by the core
 * are protected from the VPN loop with VpnService.protect().
 *
 * This replaces the previous DNS-only packet loop. Min Filter still keeps its
 * simple Android UI/rules, while the network engine now has a real userspace
 * TCP/UDP stack instead of merely creating a VPN shell.
 */
class MinFilterVpnService : VpnService(), TunInterface {
    companion object {
        const val ACTION_STOP = "com.minfilter.app.STOP"
        private val active = AtomicBoolean(false)
        @JvmStatic fun isRunning(): Boolean = active.get()
        private const val CHANNEL_ID = "min_filter_protection"
        private const val NOTIFICATION_ID = 1001
        private const val VPN_ADDRESS = "10.10.0.2/30"
        private const val VPN_DNS = "10.10.0.1"
        private const val MTU = 1400
    }

    private var tun: android.os.ParcelFileDescriptor? = null
    private val running = AtomicBoolean(false)
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("min_filter", MODE_PRIVATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopFiltering()
            stopSelf()
            return START_NOT_STICKY
        }
        startForegroundNotification()
        startFiltering()
        return START_STICKY
    }

    private fun startFiltering() {
        if (running.getAndSet(true)) return
        active.set(true)

        try {
            Clash.load(applicationInfo.nativeLibraryDir)
            check(Clash.bridgeABI() == Clash.EXPECTED_BRIDGE_ABI) {
                "Min Filter network engine ABI mismatch"
            }

            val profile = writeMihomoProfile()
            val setupResult = setupMihomo(profile)
            check(setupResult.isEmpty()) { "Min Filter network engine setup failed: $setupResult" }

            tun?.close()
            tun = Builder()
                .setSession("Min Filter")
                .setMtu(MTU)
                .addAddress(VPN_ADDRESS.substringBefore('/'), 30)
                .addDnsServer(VPN_DNS)
                .addRoute("0.0.0.0", 0)
                // Do not route Min Filter itself through its own VPN tunnel.
                .addDisallowedApplication(packageName)
                .establish()

            val established = tun ?: error("Android VPN interface could not be established")

            Clash.startTUN(
                fd = established.fileDescriptor,
                cb = this,
                device = "min-filter",
                stack = "mixed",
                address = VPN_ADDRESS,
                dns = VPN_DNS,
                mtu = MTU
            )

            Clash.setEventListener { event ->
                // Keep the listener lightweight. Runtime diagnostics can be added later.
                if (event.contains("fatal", ignoreCase = true)) {
                    android.util.Log.e("MinFilter", event)
                }
            }
        } catch (t: Throwable) {
            android.util.Log.e("MinFilter", "VPN engine start failed", t)
            running.set(false)
            active.set(false)
            try { tun?.close() } catch (_: Exception) {}
            tun = null
            try { Clash.stopTun() } catch (_: Throwable) {}
            stopSelf()
        }
    }

    private fun setupMihomo(profile: File): String {
        val latch = CountDownLatch(1)
        var result = "setup timeout"
        val home = File(filesDir, "mihomo").apply { mkdirs() }
        Clash.quickSetup(
            initParams = "{\"homeDir\":\"${jsonEscape(home.absolutePath)}\"}",
            setupParams = "{\"profile\":\"${jsonEscape(profile.absolutePath)}\"}"
        ) {
            result = it ?: ""
            latch.countDown()
        }
        return if (latch.await(15, TimeUnit.SECONDS)) result else "setup timeout"
    }

    private fun writeMihomoProfile(): File {
        val dir = File(filesDir, "mihomo").apply { mkdirs() }
        val file = File(dir, "minfilter.yaml")
        val rejectRules = BlockRules.mihomoRules(prefs)
        val ruleText = if (rejectRules.isEmpty()) {
            "  - MATCH,DIRECT"
        } else {
            rejectRules.joinToString("\n") { "  - $it" } + "\n  - MATCH,DIRECT"
        }

        // Cloudflare Family + Quad9 Secure are used as upstream resolvers.
        // The app itself does not promise that these providers can block every
        // category; domain rules above remain the source of truth for Min Filter.
        val familyDns = prefs.getBoolean("family_dns", true)
        val malwareDns = prefs.getBoolean("malware_dns", true)
        val familyNames = if (familyDns) "                - https://1.1.1.3/dns-query\n                - https://1.0.0.3/dns-query" else "                - https://1.1.1.1/dns-query\n                - https://1.0.0.1/dns-query"
        val fallbackNames = if (malwareDns) "                - https://9.9.9.11/dns-query" else "                - https://9.9.9.9/dns-query"
        val yaml = """
            mode: rule
            log-level: warning
            ipv6: false
            unified-delay: true
            tcp-concurrent: true
            keep-alive-idle: 30
            keep-alive-interval: 15

            sniffer:
              enable: true
              sniff:
                TLS:
                HTTP:
                QUIC:

            dns:
              enable: true
              listen: 0.0.0.0:53
              enhanced-mode: fake-ip
              respect-rules: true
              nameserver:
${familyNames}
              fallback:
${fallbackNames}
              fallback-filter:
                geoip: false

            rules:
            $ruleText
        """.trimIndent() + "\n"
        file.writeText(yaml)
        return file
    }

    private fun jsonEscape(value: String): String = value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")

    override fun protect(fd: Int) {
        super.protect(fd)
    }

    override fun resolverProcess(protocol: Int, source: String, target: String, uid: Int): String {
        return ""
    }

    private fun startForegroundNotification() {
        createChannel()
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Min Filter")
            .setContentText("পূর্ণাঙ্গ TCP/UDP ওয়েব ফিল্টার চালু আছে")
            .setSmallIcon(R.drawable.ic_min_filter)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Min Filter Protection",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    private fun stopFiltering() {
        if (!running.getAndSet(false)) return
        active.set(false)
        try { Clash.stopTun() } catch (_: Throwable) {}
        try { Clash.setEventListener(null) } catch (_: Throwable) {}
        try { tun?.close() } catch (_: Exception) {}
        tun = null
    }

    override fun onDestroy() {
        stopFiltering()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = super.onBind(intent)
}
