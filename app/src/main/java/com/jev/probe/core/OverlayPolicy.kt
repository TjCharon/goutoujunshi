package com.jev.probe.core

/** Small, Android-free rules used to decide whether the floating assistant belongs on screen. */
object OverlayPolicy {
    fun foregroundPackage(rootPackage: CharSequence?, eventPackage: CharSequence?): String? =
        rootPackage?.toString()?.takeIf { it.isNotBlank() }
            ?: eventPackage?.toString()?.takeIf { it.isNotBlank() }

    fun shouldHide(packageName: String?, ownPackage: String): Boolean {
        if (packageName.isNullOrBlank()) return false
        return packageName == ownPackage ||
            packageName.contains("launcher", ignoreCase = true) ||
            packageName == "com.miui.home" ||
            packageName == "com.android.systemui"
    }
}
