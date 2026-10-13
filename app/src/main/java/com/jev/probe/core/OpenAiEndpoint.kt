package com.jev.probe.core

/** Normalizes a user-provided OpenAI-compatible base URL. */
object OpenAiEndpoint {
    fun resolve(raw: String): String {
        val base = raw.trim().trimEnd('/')
        require(base.isNotBlank()) { "Base URL 不能为空" }
        require(base.startsWith("https://", ignoreCase = true)) { "Base URL 必须使用 HTTPS" }
        return if (base.endsWith("/chat/completions")) base else "$base/chat/completions"
    }
}
