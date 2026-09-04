package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberCyan

@Composable
fun CustomCoordinateDialog(
    initialLat: Double,
    initialLng: Double,
    onDismiss: () -> Unit,
    onConfirm: (lat: Double, lng: Double, name: String) -> Unit
) {
    var nameText by remember { mutableStateOf("Ubicación Manual") }
    var latText by remember { mutableStateOf(String.format("%.5f", initialLat)) }
    var lngText by remember { mutableStateOf(String.format("%.5f", initialLng)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .testTag("custom_coordinate_dialog"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Coordenadas Manuales",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Text(
                    text = "Introduce la latitud y longitud exactas de cualquier punto geográfico del planeta.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )

                // Presets
                Text(
                    text = "Atajos rápidos:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFCBD5E1)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = {
                            nameText = "Torre Eiffel"
                            latText = "48.8584"
                            lngText = "2.2945"
                        },
                        label = { Text("🗼 París", fontSize = 11.sp, color = Color.White) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1E293B)),
                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = Color(0xFF334155))
                    )
                    SuggestionChip(
                        onClick = {
                            nameText = "Times Square"
                            latText = "40.7580"
                            lngText = "-73.9855"
                        },
                        label = { Text("🏙️ NY", fontSize = 11.sp, color = Color.White) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1E293B)),
                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = Color(0xFF334155))
                    )
                    SuggestionChip(
                        onClick = {
                            nameText = "Monte Fuji"
                            latText = "35.3606"
                            lngText = "138.7274"
                        },
                        label = { Text("🗻 Fuji", fontSize = 11.sp, color = Color.White) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1E293B)),
                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = Color(0xFF334155))
                    )
                }

                // Name field
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Nombre o referencia") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_coord_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = CyberCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Latitude
                OutlinedTextField(
                    value = latText,
                    onValueChange = {
                        latText = it
                        errorMessage = null
                    },
                    label = { Text("Latitud (-90.0 a 90.0)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_coord_lat_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = CyberCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Longitude
                OutlinedTextField(
                    value = lngText,
                    onValueChange = {
                        lngText = it
                        errorMessage = null
                    },
                    label = { Text("Longitud (-180.0 a 180.0)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_coord_lng_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = CyberCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_coord_cancel_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val latClean = latText.replace(',', '.').toDoubleOrNull()
                            val lngClean = lngText.replace(',', '.').toDoubleOrNull()

                            if (latClean == null || latClean < -90.0 || latClean > 90.0) {
                                errorMessage = "Latitud inválida (debe estar entre -90 y 90)."
                            } else if (lngClean == null || lngClean < -180.0 || lngClean > 180.0) {
                                errorMessage = "Longitud inválida (debe estar entre -180 y 180)."
                            } else {
                                onConfirm(latClean, lngClean, nameText.ifBlank { "Personalizada" })
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_coord_confirm_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = Color(0xFF00363F)
                        )
                    ) {
                        Text("Activar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
