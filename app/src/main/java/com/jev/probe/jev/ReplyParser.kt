package com.jev.probe.jev

import org.json.JSONArray
import org.json.JSONObject

/** Strictly extracts three sendable replies and rejects model commentary/reasoning. */
object ReplyParser {
    private val hiddenReasoning = Regex("(?is)<(?:think|analysis|reasoning)>.*?</(?:think|analysis|reasoning)>")
    private val unclosedReasoning = Regex("(?is)^.*?</(?:think|analysis|reasoning)>")
    private val metaPhrases = listOf(
        "以下是", "候选回复", "风格分别", "根据当前对话", "根据上下文",
        "分析过程", "思考过程", "推理过程", "我的分析", "可供选择"
    )
    private val styleLabels = listOf(
        "稳健", "承接", "自然", "推进", "行动", "边界", "收束",
        "轻松化解", "直接反问", "冷处理", "会撩", "强势", "回复"
    )

    fun parseThree(raw: String): List<String> {
        val content = cleanEnvelope(raw)
        parseArrayAnywhere(content)?.let { return requireThree(it) }
        parseObjectAnywhere(content)?.let { return requireThree(it) }

        val replies = ArrayList<String>()
        content.lineSequence().forEach { original ->
            val parsed = parseFallbackLine(original) ?: return@forEach
            if (parsed !in replies) replies.add(parsed)
        }
        return requireThree(replies)
    }

    private fun cleanEnvelope(raw: String): String = raw
        .replace(hiddenReasoning, "")
        .replace(unclosedReasoning, "")
        .replace("```json", "", ignoreCase = true)
        .replace("```", "")
        .trim()

    private fun parseArrayAnywhere(content: String): List<String>? {
        val starts = content.indices.filter { content[it] == '[' }
        val ends = content.indices.filter { content[it] == ']' }
        for (start in starts) {
            for (end in ends.asReversed()) {
                if (end <= start) continue
                val parsed = runCatching { JSONArray(content.substring(start, end + 1)) }.getOrNull() ?: continue
                val out = ArrayList<String>()
                for (i in 0 until parsed.length()) {
                    val value = parsed.optString(i).trim()
                    if (isSendable(value)) out.add(value)
                }
                if (out.size >= 3) return out
            }
        }
        return null
    }

    private fun parseObjectAnywhere(content: String): List<String>? {
        val start = content.indexOf('{')
        val end = content.lastIndexOf('}')
        if (start < 0 || end <= start) return null
        val obj = runCatching { JSONObject(content.substring(start, end + 1)) }.getOrNull() ?: return null
        for (key in listOf("replies", "candidates", "候选回复")) {
            val arr = obj.optJSONArray(key) ?: continue
            val out = ArrayList<String>()
            for (i in 0 until arr.length()) {
                val value = arr.optString(i).trim()
                if (isSendable(value)) out.add(value)
            }
            if (out.size >= 3) return out
        }
        return null
    }

    private fun parseFallbackLine(original: String): String? {
        var line = original.trim()
        if (line.isBlank()) return null
        val hadListMarker = Regex("^(?:[-*•]|\\d+[.、)])\\s*").containsMatchIn(line)
        line = line.replace(Regex("^(?:[-*•]|\\d+[.、)])\\s*"), "")
            .replace("**", "").trim()

        if (metaPhrases.any { line.contains(it) }) return null
        if ((line.endsWith('：') || line.endsWith(':')) && line.length <= 24) return null

        // Models often write `1. 稳健："正文"`; discard the style label but
        // keep same-line content. A bare heading was already rejected above.
        val colon = listOf(line.indexOf('：'), line.indexOf(':')).filter { it >= 0 }.minOrNull()
        if (colon != null) {
            val prefix = line.substring(0, colon).trim()
            if (prefix.length <= 12 && styleLabels.any { prefix.contains(it) }) {
                line = line.substring(colon + 1).trim()
            }
        }

        line = line.trim().trim('"', '\'', '“', '”').trim()
        if (!isSendable(line)) return null
        // Unmarked prose is accepted only when it looks like a short chat line;
        // long explanatory paragraphs must never become a reply card.
        if (!hadListMarker && line.length > 80) return null
        return line
    }

    private fun isSendable(value: String): Boolean =
        value.isNotBlank() && value.length <= 120 &&
            metaPhrases.none { value.contains(it) } &&
            !value.startsWith("<think", ignoreCase = true)

    private fun requireThree(values: List<String>): List<String> {
        if (values.size < 3) {
            throw IllegalArgumentException("回复模型输出格式无法识别，请尝试指令遵循更好的模型")
        }
        return values.take(3)
    }
}
