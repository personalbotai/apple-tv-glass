package com.fares.glass

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent as AndroidKey
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.fares.glass.data.AppRepository
import com.fares.glass.sys.BluetoothController
import com.fares.glass.sys.SettingsShortcuts
import com.fares.glass.sys.SystemInfo
import com.fares.glass.sys.WifiController
import com.fares.glass.ui.Backdrop
import com.fares.glass.ui.CcPage
import com.fares.glass.ui.ControlCenter
import com.fares.glass.ui.Typo

class ControlCenterOverlayActivity : ComponentActivity() {

    private lateinit var repo: AppRepository
    private lateinit var cc: ControlCenter
    private val backdrop = Backdrop()
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        cc.onPermissionsResult()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Don't recreate or finish on focus loss
        window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        window.setDimAmount(0f)
        repo = AppRepository(this)
        Typo.load(this)
        cc = ControlCenter(
            this, repo, WifiController(this), BluetoothController(this), SystemInfo(this), SettingsShortcuts(this),
            onOpen = ::startSafely,
            onRequestPermissions = { permissions.launch(it) },
            onNextWallpaper = {},
            onClose = { finish() },
        )
        cc.open(CcPage.Main)

        setContent {
            val width = resources.displayMetrics.widthPixels
            val font = LocalDensity.current.fontScale
            val focusRequester = remember { FocusRequester() }

            CompositionLocalProvider(LocalDensity provides Density(width / 960f, font)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .focusRequester(focusRequester)
                        .focusable()
                        .onPreviewKeyEvent { event ->
                            val e = event.nativeKeyEvent
                            if (e.keyCode == AndroidKey.KEYCODE_BACK || e.keyCode == AndroidKey.KEYCODE_MENU || e.keyCode == 4077) {
                                if (e.action == AndroidKey.ACTION_DOWN) {
                                    finish()
                                    return@onPreviewKeyEvent true
                                }
                            }
                            cc.onKey(e)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 12.dp, end = 24.dp)
                    ) {
                        cc.Panel(backdrop, "", Modifier)
                    }
                    cc.PasswordLayer(backdrop) { focusRequester.requestFocus() }
                }
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        finish()
    }

    private fun startSafely(intent: Intent) {
        try {
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            if (intent.action != Settings.ACTION_SETTINGS) {
                try { startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (ex: Exception) {}
            }
        }
    }
}
