package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan

@Composable
fun JoystickController(
    onSteer: (Float) -> Unit,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Control Manual de Desplazamiento",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = if (isActive) "D-Pad Activo" else "Inactivo",
                fontSize = 12.sp,
                color = if (isActive) CyberCyan else Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Toca las flechas para dirigir el desplazamiento simulado en tiempo real.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // D-Pad Grid
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // North
            DirectionButton(
                icon = Icons.Default.ArrowUpward,
                contentDescription = "Norte",
                tag = "joystick_north",
                enabled = isActive,
                onClick = { onSteer(0f) }
            )

            // Middle Row: West, Center, East
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DirectionButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Oeste",
                    tag = "joystick_west",
                    enabled = isActive,
                    onClick = { onSteer(270f) }
                )

                // Center visual knob
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Color(0xFF1E293B) else Color(0xFF141D2B))
                        .border(1.5.dp, if (isActive) CyberCyan.copy(alpha = 0.5f) else Color(0xFF334155), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationSearching,
                        contentDescription = "Centro",
                        tint = if (isActive) CyberCyan else Color(0xFF64748B),
                        modifier = Modifier.size(22.dp)
                    )
                }

                DirectionButton(
                    icon = Icons.Default.ArrowForward,
                    contentDescription = "Este",
                    tag = "joystick_east",
                    enabled = isActive,
                    onClick = { onSteer(90f) }
                )
            }

            // South
            DirectionButton(
                icon = Icons.Default.ArrowDownward,
                contentDescription = "Sur",
                tag = "joystick_south",
                enabled = isActive,
                onClick = { onSteer(180f) }
            )
        }
    }
}

@Composable
private fun DirectionButton(
    icon: ImageVector,
    contentDescription: String,
    tag: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) Color(0xFF1E293B) else Color(0xFF121A28))
            .border(
                width = 1.dp,
                color = if (enabled) Color(0xFF334155) else Color(0xFF1E293B),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = CyberCyan),
                onClick = onClick
            )
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) Color.White else Color(0xFF475569),
            modifier = Modifier.size(24.dp)
        )
    }
}
