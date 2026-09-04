package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SimulationState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanMuted
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarVisualizer(
    state: SimulationState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF070C16))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("radar_visualizer_box"),
        contentAlignment = Alignment.Center
    ) {
        // Radar Drawing
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.minDimension / 2f) - 16.dp.toPx()

            // Concentric range circles
            val ringCount = 4
            for (i in 1..ringCount) {
                val r = maxRadius * (i.toFloat() / ringCount)
                drawCircle(
                    color = Color(0x1F38BDF8),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.2f)
                )
            }

            // Crosshair lines
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            drawLine(
                color = Color(0x2E38BDF8),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1f,
                pathEffect = dashEffect
            )
            drawLine(
                color = Color(0x2E38BDF8),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1f,
                pathEffect = dashEffect
            )

            // Dynamic sweeping line if active
            if (state.isActive) {
                val sweepRad = Math.toRadians(sweepAngle.toDouble())
                val sweepEnd = Offset(
                    (center.x + maxRadius * cos(sweepRad)).toFloat(),
                    (center.y + maxRadius * sin(sweepRad)).toFloat()
                )
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, CyberCyan.copy(alpha = 0.6f)),
                        start = center,
                        end = sweepEnd
                    ),
                    start = center,
                    end = sweepEnd,
                    strokeWidth = 2.5f
                )
            }

            // Target central blip
            val targetColor = if (state.isActive) CyberCyan else Color(0xFF64748B)
            drawCircle(
                color = targetColor.copy(alpha = 0.25f),
                radius = 18.dp.toPx() * if (state.isActive) pulseScale else 1f,
                center = center
            )
            drawCircle(
                color = targetColor,
                radius = 6.dp.toPx(),
                center = center
            )
        }

        // Overlay: Cardinal directions
        Text(
            text = "N",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyberCyanMuted,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Text(
            text = "S",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        Text(
            text = "E",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        Text(
            text = "O",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.align(Alignment.CenterStart)
        )

        // Overlay: Live Bearing Indicator arrow at top-right
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Rumbo",
                tint = CyberCyan,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(state.bearing)
            )
            Text(
                text = "${state.bearing.toInt()}°",
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Overlay: Live GPS Coordinates at bottom-left
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "LAT: ${String.format("%.5f", state.latitude)}",
                color = CyberCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "LON: ${String.format("%.5f", state.longitude)}",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Overlay: Speed at bottom-right
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${String.format("%.1f", state.speedKmh)} km/h",
                color = if (state.isActive) Color(0xFF38BDF8) else Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = state.mode.title,
                color = Color(0xFF64748B),
                fontSize = 10.sp
            )
        }
    }
}
