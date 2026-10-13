package com.jev.probe.capture.ocr

import org.junit.Assert.assertEquals
import org.junit.Test

class OcrSideClassifierTest {
    @Test fun rightAnchoredWechatBubbleIsMine() {
        assertEquals("me", OcrSideClassifier.classify(left = 335, right = 800, screenWidth = 946))
    }

    @Test fun longIncomingBubbleCrossingCentreRemainsTheirs() {
        assertEquals("other", OcrSideClassifier.classify(left = 148, right = 800, screenWidth = 946))
    }

    @Test fun shortLeftAnchoredBubbleIsTheirs() {
        assertEquals("other", OcrSideClassifier.classify(left = 150, right = 350, screenWidth = 946))
    }
}
