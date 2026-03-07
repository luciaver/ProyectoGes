package com.example.ProyectoGes.ui.home

import android.net.Uri
import com.example.ProyectoGes.models.UserRoles
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ProyectoGes.ui.backend.ges_reservation.ReservationViewModel
import com.example.ProyectoGes.ui.backend.ges_reservation.ReservationViewModelFactory
import com.example.ProyectoGes.ui.backend.ges_team.TeamViewModel
import com.example.ProyectoGes.ui.backend.ges_team.TeamViewModelFactory
import Routes

// ── Colores globales ─────────────────────────────────────────────────────────
val DarkBg        = Color(0xFF0D0D0D)
val DarkSurface   = Color(0xFF1A1A2E)
val CardColor     = Color(0xFF16213E)
val BlueMain      = Color(0xFF1565C0)
val BlueLight     = Color(0xFF2196F3)
val White         = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0BEC5)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    nombre: String?,
    rol: String? = "JUGADOR",
    userId: Int = 0,
    equipo: String? = null
) {
    val context = LocalContext.current
    val reservationVM: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val teamVM: TeamViewModel               = viewModel(factory = TeamViewModelFactory(context))

    val allReservations by reservationVM.reservations.collectAsState()
    val allTeams        by teamVM.teams.collectAsState()

    // Reservas del usuario actual
    LaunchedEffect(userId) {
        if (userId != 0) reservationVM.loadByUser(userId)
    }

    // Equipo completo del usuario (buscar por nombre)
    val myTeam = if (equipo != null) allTeams.find { it.nombre == equipo } else null

    val tieneEquipo = UserRoles.rolesConEquipo.contains(rol)

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GesSport", color = BlueLight, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(getRoleLabel(rol), color = TextSecondary, fontSize = 12.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { /* mapa futuro */ }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa", tint = BlueLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = BlueLight,
                        selectedTextColor   = BlueLight,
                        unselectedIconColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        val nombreEnc = Uri.encode(nombre ?: "Usuario")
                        navController.navigate("${Routes.AddReservation}/$userId/$nombreEnc")
                    },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Reservar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = BlueLight,
                        selectedTextColor   = BlueLight,
                        unselectedIconColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.popBackStack() },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    label = { Text("Salir") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Bienvenida
            item { WelcomeCard(nombre = nombre, rol = rol) }

            // Acciones rápidas
            item {
                Text("Acciones rápidas", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                val nombreEnc = Uri.encode(nombre ?: "Usuario")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon    = Icons.Default.Add,
                        label   = "Nueva\nReserva",
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("${Routes.AddReservation}/$userId/$nombreEnc") }
                    )
                    QuickActionCard(
                        icon    = Icons.Default.List,
                        label   = "Mis\nReservas",
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("${Routes.MyReservations}/$userId") }
                    )
                    if (rol == "ENTRENADOR") {
                        QuickActionCard(
                            icon    = Icons.Default.Group,
                            label   = "Gestionar\nEquipo",
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(Routes.GesTeam) }
                        )
                    }
                }
            }

            // Sección Mi Equipo (jugadores, entrenadores, árbitros con equipo asignado)
            if (tieneEquipo && equipo != null) {
                item {
                    Text("Mi Equipo", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(containerColor = CardColor),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Group,
                                contentDescription = null,
                                tint     = BlueLight,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text  = myTeam?.nombre ?: equipo,
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 16.sp
                                )
                                if (myTeam != null) {
                                    Text(
                                        text     = myTeam.deporte,
                                        color    = BlueLight,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text     = "${myTeam.numJugadores} jugadores",
                                        color    = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Próximas reservas
            item {
                Text("Mis próximas reservas", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (allReservations.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(containerColor = CardColor),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No tienes reservas próximas", color = TextSecondary)
                        }
                    }
                }
            } else {
                items(allReservations.take(5)) { reservation ->
                    ReservationCard(
                        fecha    = reservation.fecha,
                        hora     = "${reservation.horaInicio} - ${reservation.horaFin}",
                        lugar    = reservation.instalacionNombre,
                        onCancelar = { reservationVM.deleteReservation(reservation.id) }
                    )
                }
            }

            // Notificar incidencia (solo entrenadores)
            if (rol == "ENTRENADOR") {
                item { IncidenciaCard() }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun WelcomeCard(nombre: String?, rol: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = BlueMain),
        shape    = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = White, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text       = "¡Hola, ${nombre ?: "Usuario"}!",
                    color      = White,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text     = getRoleLabel(rol),
                    color    = White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        colors   = CardDefaults.cardColors(containerColor = CardColor),
        shape    = RoundedCornerShape(12.dp),
        onClick  = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = BlueLight, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text      = label,
                color     = White,
                fontSize  = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ReservationCard(fecha: String, hora: String, lugar: String, onCancelar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = CardColor),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = BlueLight, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(lugar, color = White, fontWeight = FontWeight.Bold)
                Text(fecha, color = TextSecondary, fontSize = 13.sp)
                Text(hora, color = BlueLight, fontSize = 13.sp)
            }
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = Color(0xFFEF5350), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun IncidenciaCard() {
    var texto   by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = CardColor),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFA726))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notificar incidencia", color = White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (enviado) {
                Text("✓ Incidencia enviada", color = Color(0xFF4CAF50))
            } else {
                OutlinedTextField(
                    value         = texto,
                    onValueChange = { texto = it },
                    placeholder   = { Text("Ej: Entrenamiento cancelado...", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = BlueLight,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor     = White,
                        unfocusedTextColor   = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { if (texto.isNotBlank()) enviado = true },
                    colors  = ButtonDefaults.buttonColors(containerColor = BlueMain),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Enviar", color = White) }
            }
        }
    }
}

fun getRoleLabel(rol: String?): String = when (rol) {
    "ADMIN_DEPORTIVO" -> "Administrador"
    "ENTRENADOR"      -> "Entrenador"
    "JUGADOR"         -> "Jugador"
    "ARBITRO"         -> "Árbitro"
    else              -> "Usuario"
}