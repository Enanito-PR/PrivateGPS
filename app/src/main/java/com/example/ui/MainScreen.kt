package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WorldCitiesRepository
import com.example.model.CityLocation
import com.example.model.MovementMode
import com.example.model.SimulationState
import com.example.ui.components.CustomCoordinateDialog
import com.example.ui.components.JoystickController
import com.example.ui.components.RadarVisualizer
import com.example.ui.theme.AccentDanger
import com.example.ui.theme.AccentSuccess
import com.example.ui.theme.AccentWarning
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: LocationPrivacyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.simulationState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedContinent by viewModel.selectedContinent.collectAsState()
    val filteredCities by viewModel.filteredCities.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showCustomCoordinateDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF070C16),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A))
                                .border(1.dp, CyberCyan.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (state.isActive) CyberCyan else Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Privacidad GPS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (state.isActive) "Escudo Activo • ${state.currentCity}" else "Escudo en Pausa",
                                fontSize = 11.sp,
                                color = if (state.isActive) CyberCyan else Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCustomCoordinateDialog = true },
                        modifier = Modifier.testTag("btn_open_custom_coords")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddLocationAlt,
                            contentDescription = "Coordenadas manuales",
                            tint = CyberCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1120),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0B1120),
                contentColor = Color.White,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Control"
                        )
                    },
                    label = { Text("Control") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        selectedTextColor = CyberCyan,
                        indicatorColor = Color(0xFF132238),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("nav_tab_control")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Ciudades"
                        )
                    },
                    label = { Text("Ciudades") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        selectedTextColor = CyberCyan,
                        indicatorColor = Color(0xFF132238),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("nav_tab_cities")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Ajustes"
                        )
                    },
                    label = { Text("Persistencia") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberCyan,
                        selectedTextColor = CyberCyan,
                        indicatorColor = Color(0xFF132238),
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> ControlDashboardTab(
                    state = state,
                    onToggle = { viewModel.toggleService() },
                    onModeChange = { viewModel.setMovementMode(it) },
                    onSpeedChange = { viewModel.setSpeed(it) },
                    onSteer = { viewModel.steerManual(it) },
                    onOpenDeveloperSettings = { viewModel.openDeveloperOptions(context) },
                    onOpenCustomCoords = { showCustomCoordinateDialog = true }
                )
                1 -> CitiesCatalogTab(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.onSearchQueryChanged(it) },
                    selectedContinent = selectedContinent,
                    onContinentSelect = { viewModel.onContinentSelected(it) },
                    cities = filteredCities,
                    activeCityName = state.currentCity,
                    isProtected = state.isActive,
                    onSelectCity = { viewModel.startWithCity(it) },
                    onOpenCustomCoords = { showCustomCoordinateDialog = true }
                )
                2 -> PersistenceSettingsTab(
                    state = state,
                    onTogglePersistBoot = { viewModel.setPersistOnBoot(it) },
                    onOpenDeveloperSettings = { viewModel.openDeveloperOptions(context) }
                )
            }
        }

        if (showCustomCoordinateDialog) {
            CustomCoordinateDialog(
                initialLat = state.latitude,
                initialLng = state.longitude,
                onDismiss = { showCustomCoordinateDialog = false },
                onConfirm = { lat, lng, name ->
                    viewModel.setCustomCoordinates(lat, lng, name)
                }
            )
        }
    }
}

@Composable
fun ControlDashboardTab(
    state: SimulationState,
    onToggle: () -> Unit,
    onModeChange: (MovementMode) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onSteer: (Float) -> Unit,
    onOpenDeveloperSettings: () -> Unit,
    onOpenCustomCoords: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shield Hero Card
        ShieldStatusHeroCard(
            state = state,
            onToggle = onToggle
        )

        // Developer Options Guidance Banner (if mock location provider error)
        if (state.mockProviderError != null && state.isActive) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("developer_options_warning_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF291515)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentDanger.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AccentDanger,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Ajuste de Desarrollador Requerido",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB4AB)
                        )
                    }
                    Text(
                        text = "Para que todas las aplicaciones del dispositivo lean la ubicación simulada, debes seleccionarnos en 'Opciones de desarrollador' -> 'Elegir aplicación para simular ubicación'.",
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0)
                    )
                    Button(
                        onClick = onOpenDeveloperSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentDanger),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_dev_options")
                    ) {
                        Text("Abrir Ajustes de Desarrollador", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Live Telemetry Grid
        TelemetryGrid(state = state)

        // Radar Simulation Visualizer
        RadarVisualizer(state = state)

        // Movement Mode Selector
        MovementModeSelector(
            currentMode = state.mode,
            onModeSelect = onModeChange
        )

        // Speed Slider Card
        SpeedControlCard(
            currentSpeed = state.speedKmh,
            mode = state.mode,
            onSpeedChange = onSpeedChange
        )

        // Virtual D-Pad / Steering Joystick
        JoystickController(
            onSteer = onSteer,
            isActive = state.isActive
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ShieldStatusHeroCard(
    state: SimulationState,
    onToggle: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = if (state.isActive) CyberCyan.copy(alpha = 0.7f) else Color(0xFF1E293B),
        label = "border_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.5.dp, cardBorderColor, RoundedCornerShape(24.dp))
            .testTag("shield_status_hero_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (state.isActive) Color(0xFF0C172B) else Color(0xFF0F172A)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shield Status Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (state.isActive) Color(0x2E00E5FF) else Color(0xFF1E293B))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (state.isActive) CyberCyan else Color(0xFF64748B))
                )
                Text(
                    text = if (state.isActive) "ESCUDO ACTIVO Y PROTEGIENDO" else "PROTECCIÓN DESACTIVADA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (state.isActive) CyberCyan else Color(0xFF94A3B8)
                )
            }

            // Big Central Shield Emblem
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(if (state.isActive) glowScale else 1f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (state.isActive) {
                                listOf(CyberCyan.copy(alpha = 0.35f), Color.Transparent)
                            } else {
                                listOf(Color(0xFF1E293B), Color.Transparent)
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (state.isActive) Color(0xFF0B2138) else Color(0xFF162032))
                        .border(
                            width = 2.dp,
                            color = if (state.isActive) CyberCyan else Color(0xFF334155),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isActive) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = if (state.isActive) CyberCyan else Color(0xFF64748B),
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            // Location details
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.currentCity,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = state.currentCountry,
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "${String.format("%.4f", state.latitude)}, ${String.format("%.4f", state.longitude)}",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberCyanMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Main Action Toggle Button
            Button(
                onClick = onToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_toggle_protection"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isActive) Color(0xFFEF4444) else CyberCyan,
                    contentColor = if (state.isActive) Color.White else Color(0xFF00363F)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = if (state.isActive) Icons.Default.LockOpen else Icons.Default.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isActive) "Detener Protección GPS" else "Activar Protección de Ubicación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun TelemetryGrid(state: SimulationState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TelemetryCard(
            title = "Velocidad",
            value = "${String.format("%.1f", state.speedKmh)} km/h",
            subtitle = state.mode.title,
            modifier = Modifier.weight(1f)
        )
        TelemetryCard(
            title = "Rumbo",
            value = "${state.bearing.toInt()}°",
            subtitle = getCardinalLabel(state.bearing),
            modifier = Modifier.weight(1f)
        )
        TelemetryCard(
            title = "Precisión",
            value = "±${String.format("%.1f", state.accuracyMeters)}m",
            subtitle = "Deriva natural",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TelemetryCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = CyberCyanMuted
            )
        }
    }
}

@Composable
fun MovementModeSelector(
    currentMode: MovementMode,
    onModeSelect: (MovementMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Modo de Desplazamiento Realista",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Anti-Rastreo",
                fontSize = 11.sp,
                color = AccentSuccess,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = "Simula movimientos humanos por las calles de la ciudad para evadir algoritmos de detección estática.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(MovementMode.values()) { mode ->
                val isSelected = currentMode == mode
                val icon = when (mode) {
                    MovementMode.JITTER_STATIC -> Icons.Default.PinDrop
                    MovementMode.WALKING -> Icons.Default.DirectionsWalk
                    MovementMode.CYCLING -> Icons.Default.DirectionsBike
                    MovementMode.DRIVING -> Icons.Default.DirectionsCar
                    MovementMode.SMART_PATROL -> Icons.Default.Sync
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF0B253D) else Color(0xFF162032),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) CyberCyan else Color(0xFF24324D)
                    ),
                    modifier = Modifier
                        .clickable { onModeSelect(mode) }
                        .testTag("mode_chip_${mode.name}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) CyberCyan else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = mode.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }

        // Mode details text
        Text(
            text = currentMode.description,
            fontSize = 12.sp,
            color = Color(0xFF38BDF8),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF091220))
                .padding(10.dp)
        )
    }
}

@Composable
fun SpeedControlCard(
    currentSpeed: Float,
    mode: MovementMode,
    onSpeedChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Ajuste Fino de Velocidad",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = "${String.format("%.1f", currentSpeed)} km/h",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                fontFamily = FontFamily.Monospace
            )
        }

        Slider(
            value = currentSpeed,
            onValueChange = onSpeedChange,
            valueRange = 0.5f..120f,
            colors = SliderDefaults.colors(
                thumbColor = CyberCyan,
                activeTrackColor = CyberCyan,
                inactiveTrackColor = Color(0xFF1E293B)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("speed_slider")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Paseo (3-6)", fontSize = 10.sp, color = Color(0xFF64748B))
            Text("Bicicleta (15-25)", fontSize = 10.sp, color = Color(0xFF64748B))
            Text("Coche (45-90)", fontSize = 10.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun CitiesCatalogTab(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedContinent: String,
    onContinentSelect: (String) -> Unit,
    cities: List<CityLocation>,
    activeCityName: String,
    isProtected: Boolean,
    onSelectCity: (CityLocation) -> Unit,
    onOpenCustomCoords: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search & Custom Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Buscar ciudad o país...", color = Color(0xFF64748B)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = CyberCyan
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("city_search_bar"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = Color(0xFF1E293B),
                    focusedContainerColor = Color(0xFF0F172A),
                    unfocusedContainerColor = Color(0xFF0F172A),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            ElevatedButton(
                onClick = onOpenCustomCoords,
                modifier = Modifier
                    .height(54.dp)
                    .testTag("btn_custom_coords_in_cities"),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = CyberCyan
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddLocationAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Manual", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Continent category chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(WorldCitiesRepository.continents) { continent ->
                val isSelected = selectedContinent == continent
                FilterChip(
                    selected = isSelected,
                    onClick = { onContinentSelect(continent) },
                    label = { Text(continent, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = Color(0xFF00363F),
                        containerColor = Color(0xFF0F172A),
                        labelColor = Color(0xFFCBD5E1)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) CyberCyan else Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("chip_continent_$continent")
                )
            }
        }

        // Cities Count
        Text(
            text = "${cities.size} ciudades disponibles para cambiar ubicación",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        // Cities List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("cities_lazy_column"),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(cities, key = { it.id }) { city ->
                CityItemCard(
                    city = city,
                    isCurrent = city.name.equals(activeCityName, ignoreCase = true) && isProtected,
                    onSelect = { onSelectCity(city) }
                )
            }
        }
    }
}

@Composable
fun CityItemCard(
    city: CityLocation,
    isCurrent: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (isCurrent) CyberCyan else Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .testTag("city_card_${city.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color(0xFF0E1F36) else Color(0xFF0F172A)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = city.flagEmoji,
                    fontSize = 26.sp
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = city.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVA",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                            }
                        }
                    }
                    Text(
                        text = "${city.country} • ${city.continent}",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "Lat: ${String.format("%.4f", city.latitude)}, Lon: ${String.format("%.4f", city.longitude)}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrent) AccentSuccess else Color(0xFF1E293B),
                    contentColor = if (isCurrent) Color.White else CyberCyan
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_select_city_${city.id}")
            ) {
                if (isCurrent) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Protegido", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Mover Aquí", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun PersistenceSettingsTab(
    state: SimulationState,
    onTogglePersistBoot: (Boolean) -> Unit,
    onOpenDeveloperSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Reboot Persistence
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                .testTag("boot_persistence_card"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Persistencia Permanente tras Reinicio",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Auto-arranque en segundo plano",
                                fontSize = 11.sp,
                                color = AccentSuccess
                            )
                        }
                    }
                    Switch(
                        checked = state.persistOnBoot,
                        onCheckedChange = onTogglePersistBoot,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00363F),
                            checkedTrackColor = CyberCyan,
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("switch_persist_boot")
                    )
                }

                Text(
                    text = "Cuando esta opción está activada, el servicio en segundo plano se reanudará automáticamente al encender o reiniciar el teléfono celular. Mantendrá la ciudad y coordenadas seleccionadas sin interrupciones.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }

        // Section 2: Anti-detection & Natural Drift Jitter
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PinDrop,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Micro-deriva GPS Natural",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Bypass de detección estática",
                                fontSize = 11.sp,
                                color = CyberCyanMuted
                            )
                        }
                    }
                    Switch(
                        checked = state.naturalJitterEnabled,
                        onCheckedChange = { /* handled by state */ },
                        enabled = true,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00363F),
                            checkedTrackColor = CyberCyan
                        ),
                        modifier = Modifier.testTag("switch_natural_jitter")
                    )
                }

                Text(
                    text = "Genera fluctuaciones aleatorias microscópicas (1-3 metros) y variabilidad de altitud y precisión satelital para engañar detectores de trampas de aplicaciones de terceros y redes sociales.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }

        // Section 3: Android Developer Options Configuration Guide
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Guía de Activación en Android",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                ConfigStepItem(
                    stepNumber = "1",
                    title = "Activar Opciones de Desarrollador",
                    description = "Ve a Ajustes -> Acerca del teléfono y toca 7 veces consecutivas sobre 'Número de compilación'."
                )

                ConfigStepItem(
                    stepNumber = "2",
                    title = "Elegir Aplicación para Simular Ubicación",
                    description = "Entra en Opciones de desarrollador, busca 'Elegir aplicación para simular ubicación' (Mock Location) y selecciona 'Privacidad GPS'."
                )

                ConfigStepItem(
                    stepNumber = "3",
                    title = "Listo para Navegar con Privacidad",
                    description = "Una vez asignada, cualquier aplicación del sistema operativo (mapas, navegadores, servicios) verá la ubicación seleccionada."
                )

                Button(
                    onClick = onOpenDeveloperSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = CyberCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_dev_options_guide")
                ) {
                    Text("Abrir Ajustes de Desarrollador de Android", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 4: Privacy & Anti-tracking Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091220))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔒 Protección Frente al Rastreo de Terceros",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
                Text(
                    text = "Los servicios de publicidad, redes sociales y apps en segundo plano recolectan continuamente tus coordenadas GPS reales para elaborar perfiles comerciales y restringir contenidos por país o región. Privacidad GPS reemplaza tus datos de ubicación en tiempo real tanto en el proveedor GPS como en el proveedor de Red (Network Provider) para blindar tu anonimato.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ConfigStepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(1.dp, CyberCyan.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 16.sp
            )
        }
    }
}

private fun getCardinalLabel(bearing: Float): String {
    val b = (bearing % 360 + 360) % 360
    return when {
        b >= 337.5 || b < 22.5 -> "Norte"
        b in 22.5..67.5 -> "Noreste"
        b in 67.5..112.5 -> "Este"
        b in 112.5..157.5 -> "Sureste"
        b in 157.5..202.5 -> "Sur"
        b in 202.5..247.5 -> "Suroeste"
        b in 247.5..292.5 -> "Oeste"
        else -> "Noroeste"
    }
}
