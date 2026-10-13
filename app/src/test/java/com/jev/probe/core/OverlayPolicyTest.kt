package com.jev.probe.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayPolicyTest {
    @Test fun eventPackageIsUsedWhenWechatHidesTheActiveRoot() {
        assertEquals("com.tencent.mm", OverlayPolicy.foregroundPackage(null, "com.tencent.mm"))
    }

    @Test fun activeRootWinsOverKeyboardOrSystemEventPackage() {
        assertEquals(
            "com.tencent.mm",
            OverlayPolicy.foregroundPackage("com.tencent.mm", "com.tencent.wetype")
        )
    }

    @Test fun normalAppsKeepTheBubbleButOwnAndSystemScreensHideIt() {
        assertFalse(OverlayPolicy.shouldHide("com.tencent.mm", "com.goutoujunshi.overlay"))
        assertTrue(OverlayPolicy.shouldHide("com.goutoujunshi.overlay", "com.goutoujunshi.overlay"))
        assertTrue(OverlayPolicy.shouldHide("com.android.systemui", "com.goutoujunshi.overlay"))
    }
}
