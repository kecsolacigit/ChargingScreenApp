package com.charging.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 13+ notif engedély: egyszer kérjük, hogy a töltés-eseménynél biztosan tudjunk full-screen notit küldeni
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        // Lock screen + screen on (biztosabb, mint csak manifest)
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContent { ChargingScreenUI(batteryLevel = getBatteryLevel()) }
    }

    private fun getBatteryLevel(): Int {
        val bm = getSystemService<BatteryManager>() ?: return 0
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).coerceIn(0, 100)
    }
}

@Composable
fun ChargingScreenUI(batteryLevel: Int) {
    val progress by animateFloatAsState(
        targetValue = batteryLevel / 100f,
        animationSpec = tween(900),
        label = "batteryProgress"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF000000)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(260.dp)) {
                    // háttér kör
                    drawArc(
                        color = Color(0xFF2A2A2A),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 28f, cap = StrokeCap.Round)
                    )
                    // progress kör
                    drawArc(
                        color = Color(0xFF00FFAA),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 28f, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "$batteryLevel%",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Charging…",
                fontSize = 18.sp,
                color = Color(0xFF00FFAA)
            )
        }
    }
}
