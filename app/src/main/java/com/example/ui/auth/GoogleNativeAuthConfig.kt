package com.example.ui.auth

import com.example.data.SupabaseConfig
import java.security.MessageDigest
import java.security.SecureRandom

object GoogleNativeAuthConfig {
    const val WEB_CLIENT_ID = SupabaseConfig.GOOGLE_WEB_CLIENT_ID

    private val secureRandom = SecureRandom()

    fun generateRawNonce(byteLength: Int = 32): String {
        require(byteLength >= 16) { "Nonce must contain at least 16 random bytes" }
        val bytes = ByteArray(byteLength)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString(separator = "") { byte -> "%02x".format(byte.toInt() and 0xff) }
    }

    fun sha256Hex(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString(separator = "") { byte -> "%02x".format(byte.toInt() and 0xff) }
}
