package com.minfilter.app.data

import android.content.SharedPreferences

/**
 * Single source of truth for Min Filter domain rules.
 *
 * The rule engine is intentionally domain-based: it can block a hostname when
 * the network engine has a domain signal (DNS/SNI/HTTP/QUIC metadata). It does
 * not claim to classify encrypted page content.
 */
object BlockRules {
    private val shortVideoDomains = linkedSetOf(
        "tiktok.com",
        "tiktokv.com",
        "tiktokcdn.com",
        "musical.ly",
        "likee.video",
        "likee.com",
        "snackvideo.com",
        "kwai.com",
        "kwai.net"
    )

    private val optionalPlatformDomains = mapOf(
        "block_youtube" to linkedSetOf("youtube.com", "youtube-nocookie.com"),
        "block_instagram" to linkedSetOf("instagram.com", "cdninstagram.com")
    )

    fun blocked(host: String, prefs: SharedPreferences): Boolean {
        val h = normalize(host)
        if (h.isEmpty()) return false
        if (!prefs.getBoolean("dns_filter", true)) return false
        if (isWhitelisted(h, prefs)) return false

        if (prefs.getBoolean("short_video_filter", true) &&
            shortVideoDomains.any { matches(h, it) }) return true

        optionalPlatformDomains.forEach { (key, domains) ->
            if (prefs.getBoolean(key, false) && domains.any { matches(h, it) }) return true
        }

        return customBlockedDomains(prefs).any { matches(h, it) }
    }

    /** Returns the ordered mihomo rules for the current settings. */
    fun mihomoRules(prefs: SharedPreferences): List<String> = buildList {
        // Whitelist rules must be emitted before REJECT rules.
        parseDomainList(prefs.getString("trusted_domains", "") ?: "")
            .forEach { add("DOMAIN-SUFFIX,$it,DIRECT") }

        if (prefs.getBoolean("dns_filter", true)) {
            if (prefs.getBoolean("short_video_filter", true)) {
                shortVideoDomains.forEach { add("DOMAIN-SUFFIX,$it,REJECT") }
            }
            optionalPlatformDomains.forEach { (key, domains) ->
                if (prefs.getBoolean(key, false)) domains.forEach { add("DOMAIN-SUFFIX,$it,REJECT") }
            }
            customBlockedDomains(prefs).forEach { add("DOMAIN-SUFFIX,$it,REJECT") }
        }
    }

    fun normalize(host: String): String = host
        .trim()
        .lowercase()
        .trim('.')
        .removeSuffix(".")

    fun parseDomainList(raw: String): List<String> = raw
        .split('\n', ',', ';', ' ', '\t', '\r')
        .asSequence()
        .map(::normalize)
        .filter { it.isNotEmpty() }
        .filter { it.contains('.') && !it.contains('/') && !it.contains(':') }
        .distinct()
        .toList()

    private fun customBlockedDomains(prefs: SharedPreferences): List<String> =
        parseDomainList(prefs.getString("custom_block_domains", "") ?: "")

    private fun isWhitelisted(host: String, prefs: SharedPreferences): Boolean =
        parseDomainList(prefs.getString("trusted_domains", "") ?: "")
            .any { matches(host, it) }

    private fun matches(host: String, domain: String): Boolean =
        host == domain || host.endsWith(".$domain")
}
