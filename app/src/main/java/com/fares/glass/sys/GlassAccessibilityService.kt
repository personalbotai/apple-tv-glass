package com.fares.glass.sys

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.fares.glass.MainActivity

class GlassAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                4020 -> {
                    // Global Blackout / Screen off trigger
                    val intent = Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra("action", "toggle_blackout")
                    }
                    startActivity(intent)
                    return true
                }
                4065 -> {
                    // Global SmartTube launch
                    packageManager.getLaunchIntentForPackage("org.smarttube.stable")?.let {
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(it)
                    }
                    return true
                }
                4077 -> {
                    // Global Control Center toggle
                    val intent = Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra("action", "toggle_control_center")
                    }
                    startActivity(intent)
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }
}
