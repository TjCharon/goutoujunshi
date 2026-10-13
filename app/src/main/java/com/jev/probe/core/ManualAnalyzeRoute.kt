package com.jev.probe.core

/** Manual analysis must always have a route; a missing node snapshot falls back to screen OCR. */
enum class ManualAnalyzeRoute { SNAPSHOT, SCREEN_OCR }

fun manualAnalyzeRoute(snapshotAvailable: Boolean): ManualAnalyzeRoute =
    if (snapshotAvailable) ManualAnalyzeRoute.SNAPSHOT else ManualAnalyzeRoute.SCREEN_OCR

/** Reply suggestions are only valid when the newest message came from the other person. */
fun shouldDraftReply(latestFrom: String?): Boolean = latestFrom == "other"
