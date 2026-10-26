package com.fares.glass.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KeyTestOverlay(onDismiss: () -> Unit) {
    var lastCode by remember { mutableStateOf(0) }
    var lastName by remember { mutableStateOf("Tekan tombol apa saja pada remote...") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC000000))
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    val code = event.nativeKeyEvent.keyCode
                    val name = event.nativeKeyEvent.keyCodeToString(code)
                    lastCode = code
                    lastName = name
                    if (code == android.view.KeyEvent.KEYCODE_BACK) {
                        onDismiss()
                    }
                }
                true
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("PENGUJI TOMBOL REMOTE (KEY TESTER)", color = Color.Yellow, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Kode KeyCode: $lastCode", color = Color.White, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nama Tombol: $lastName", color = Color.Cyan, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(24.sp))
            Text("Tekan tombol BACK di remote untuk keluar", color = Color.Gray, fontSize = 14.sp)
        }
    }
}
