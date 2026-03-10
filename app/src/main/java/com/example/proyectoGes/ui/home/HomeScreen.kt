package com.example.proyectoGes.ui.home

import android.net.Uri
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
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.backend.ges_match.MatchViewModel
import com.example.proyectoGes.ui.backend.ges_match.MatchViewModelFactory
import com.example.proyectoGes.ui.backend.ges_reservation.ReservationViewModel
import com.example.proyectoGes.ui.backend.ges_reservation.ReservationViewModelFactory
import com.example.proyectoGes.ui.backend.ges_team.TeamViewModel
import com.example.proyectoGes.ui.backend.ges_team.TeamViewModelFactory

val BgColor      = Color(0xFFF0F4FF)
val SurfaceColor = Color(0xFFFFFFFF)
val CardBg       = Color(0xFFE8EEFF)
val PrimaryBlue  = Color(0xFF2D5BE3)
val AccentIndigo = Color(0xFF4F46E5)
val TextPrimary  = Color(0xFF1A1F36)
val TextMuted    = Color(0xFF6B7280)
val DangerRed    = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF10B981)

val DarkBg        = BgColor
val DarkSurface   = SurfaceColor
val CardColor     = CardBg
val BlueMain      = PrimaryBlue
val BlueLight     = AccentIndigo
val White         = SurfaceColor
val TextSecondary = TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userId: Int = 0,
    nombre: String?,
    rol: String? = "JUGADOR",
    equipo: String? = null
) {
    val context = LocalContext.current
    val reservationVM: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val teamVM: TeamViewModel               = viewModel(factory = TeamViewModelFactory(context))
    val matchVM: MatchViewModel             = viewModel(factory = MatchViewModelFactory(context))

    val reservations by reservationVM.reservations.collectAsState()
    val allTeams     by teamVM.teams.collectAsState()
    val matches      by matchVM.matches.collectAsState()

    LaunchedEffect(userId, equipo, rol) {
        if (userId != 0) reservationVM.loadByUser(userId)
        equipo?.let { matchVM.loadByEquipo(it) }
        if (rol == "ARBITRO") matchVM.loadByArbitro(userId)
    }

    val myTeam = allTeams.find { it.nombre == equipo }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GesSport", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(getRoleLabel(rol), color = TextMuted, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = SurfaceColor) {
                NavigationBarItem(
                    selected = true, onClick = {},
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        unselectedIconColor = TextMuted
                    )
                )
                if (rol != "ARBITRO") {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            val enc = Uri.encode(nombre ?: "Usuario")
                            navController.navigate("${Routes.AddReservation}/$userId/$enc")
                        },
                        icon = { Icon(Icons.Default.DateRange, null) },
                        label = { Text("Reservar") },
                        colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextMuted)
                    )
                }
                NavigationBarItem(
                    selected = false, onClick = { navController.popBackStack() },
                    icon = { Icon(Icons.Default.ExitToApp, null) },
                    label = { Text("Salir") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextMuted)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("¡Hola, ${nombre ?: "Usuario"}!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(getRoleLabel(rol), color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                }
            }

            if (rol == "ARBITRO") {
                item { SectionLabel("Partidos a arbitrar") }
                if (matches.isEmpty()) {
                    item { EmptyCard("No tienes partidos asignados") }
                } else {
                    items(matches) { match ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${match.equipo1} vs ${match.equipo2}", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("${match.fecha} - ${match.hora}", color = TextMuted, fontSize = 13.sp)
                                Text(match.resultado, color = PrimaryBlue, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                item {
                    SectionLabel("Acciones rápidas")
                    Spacer(Modifier.height(8.dp))
                    val enc = Uri.encode(nombre ?: "Usuario")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        QuickAction(Icons.Default.Add, "Nueva\nReserva", Modifier.weight(1f)) {
                            navController.navigate("${Routes.AddReservation}/$userId/$enc")
                        }
                        QuickAction(Icons.Default.List, "Mis\nReservas", Modifier.weight(1f)) {
                            navController.navigate("${Routes.MyReservations}/$userId")
                        }
                        if (rol == "ENTRENADOR") {
                            QuickAction(Icons.Default.Group, "Mi\nEquipo", Modifier.weight(1f)) {
                                navController.navigate(Routes.GesTeam)
                            }
                        }
                    }
                }

                if (equipo != null) {
                    item {
                        SectionLabel("Mi Equipo")
                        Spacer(Modifier.height(8.dp))
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(myTeam?.nombre ?: equipo, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    myTeam?.let { Text(it.deporte, color = PrimaryBlue, fontSize = 13.sp) }
                                }
                            }
                        }
                    }
                }

                item { SectionLabel("Mis próximas reservas") }
                if (reservations.isEmpty()) {
                    item { EmptyCard("No tienes reservas") }
                } else {
                    items(reservations.take(5)) { res ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(res.instalacionNombre, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(res.fecha, color = TextMuted, fontSize = 13.sp)
                                    Text("${res.horaInicio} - ${res.horaFin}", color = PrimaryBlue, fontSize = 13.sp)
                                    Text("%.0f€".format(res.precio), color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                TextButton(onClick = { reservationVM.deleteReservation(res.id) }) {
                                    Text("Cancelar", color = DangerRed, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item { SectionLabel("Mis partidos") }
                if (matches.isEmpty()) {
                    item { EmptyCard("No hay partidos programados") }
                } else {
                    items(matches) { match ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${match.equipo1} vs ${match.equipo2}", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("${match.fecha} - ${match.hora}", color = TextMuted, fontSize = 13.sp)
                                Text(match.resultado, color = PrimaryBlue, fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (rol == "ENTRENADOR") {
                    item { IncidenciaCard() }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
}

@Composable
fun EmptyCard(msg: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(msg, color = TextMuted)
        }
    }
}

@Composable
fun QuickAction(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), onClick = onClick) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, color = TextPrimary, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun IncidenciaCard() {
    var texto   by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null, tint = Color(0xFFF59E0B))
                Spacer(Modifier.width(8.dp))
                Text("Notificar incidencia", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            if (enviado) {
                Text("✓ Incidencia enviada", color = SuccessGreen)
            } else {
                OutlinedTextField(
                    value = texto, onValueChange = { texto = it },
                    placeholder = { Text("Describe la incidencia...", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = TextMuted, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { if (texto.isNotBlank()) enviado = true }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue), modifier = Modifier.fillMaxWidth()) {
                    Text("Enviar", color = Color.White)
                }
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