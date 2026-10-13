package com.jev.probe.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ManualAnalyzeRouteTest {
    @Test fun missingWechatNodeSnapshotFallsBackToScreenOcr() {
        assertEquals(ManualAnalyzeRoute.SCREEN_OCR, manualAnalyzeRoute(snapshotAvailable = false))
    }

    @Test fun readableConversationUsesTheExistingSnapshot() {
        assertEquals(ManualAnalyzeRoute.SNAPSHOT, manualAnalyzeRoute(snapshotAvailable = true))
    }

    @Test fun neverDraftAReplyToTheUsersOwnLatestMessage() {
        assertEquals(false, shouldDraftReply("me"))
        assertEquals(true, shouldDraftReply("other"))
    }
}
