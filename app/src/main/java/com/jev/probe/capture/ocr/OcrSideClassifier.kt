package com.jev.probe.capture.ocr

/**
 * Infer chat-bubble ownership from horizontal anchoring.
 *
 * Comparing the two outer margins is more reliable than testing whether the
 * text centre crosses the screen centre: a long incoming bubble may cross the
 * centre but is still anchored to the left edge.
 */
object OcrSideClassifier {
    fun classify(left: Int, right: Int, screenWidth: Int): String {
        if (screenWidth <= 0) return "other"
        val leftMargin = left.coerceIn(0, screenWidth)
        val rightMargin = (screenWidth - right).coerceIn(0, screenWidth)
        val requiredLead = (screenWidth * 0.06f).toInt()
        return if (leftMargin - rightMargin > requiredLead) "me" else "other"
    }
}
