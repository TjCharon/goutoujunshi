package com.jev.probe.core

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Encrypts API keys with a non-exportable Android Keystore key. */
internal class SecureValues(private val sp: SharedPreferences) {

    fun get(name: String): String {
        val stored = sp.getString(name, "") ?: ""
        if (stored.isBlank()) return ""
        if (!stored.startsWith(PREFIX)) {
            // One-time migration for keys saved by the upstream project.
            put(name, stored)
            return stored
        }
        return runCatching {
            val parts = stored.removePrefix(PREFIX).split(':', limit = 2)
            require(parts.size == 2)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateKey(),
                GCMParameterSpec(128, Base64.decode(parts[0], Base64.NO_WRAP))
            )
            cipher.updateAAD(name.toByteArray(StandardCharsets.UTF_8))
            String(
                cipher.doFinal(Base64.decode(parts[1], Base64.NO_WRAP)),
                StandardCharsets.UTF_8
            )
        }.getOrDefault("")
    }

    fun put(name: String, plainText: String) {
        if (plainText.isBlank()) {
            sp.edit().remove(name).apply()
            return
        }
        val value = runCatching {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            cipher.updateAAD(name.toByteArray(StandardCharsets.UTF_8))
            val encrypted = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
            PREFIX + Base64.encodeToString(cipher.iv, Base64.NO_WRAP) + ":" +
                Base64.encodeToString(encrypted, Base64.NO_WRAP)
        }.getOrElse { throw IllegalStateException("无法使用 Android Keystore 保存密钥", it) }
        sp.edit().putString(name, value).apply()
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val KEY_ALIAS = "goutoujunshi_api_keys_v1"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val PREFIX = "encv1:"
    }
}
