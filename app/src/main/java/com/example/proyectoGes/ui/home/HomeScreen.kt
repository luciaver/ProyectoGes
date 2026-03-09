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

val DarkBg        = Color(0xFF0D0D0D)
val DarkSurface   = Color(0xFF1A1A2E)
val CardColor     = Color(0xFF16213E)
val BlueMain      = Color(0xFF1565C0)
val BlueLight     = Color(0xFF2196F3)
val White         = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB0BEC5)

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
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GesSport", color = BlueLight, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(getRoleLabel(rol), color = TextSecondary, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                NavigationBarItem(
                    selected = true, onClick = {},
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = BlueLight, selectedTextColor = BlueLight, unselectedIconColor = TextSecondary)
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
                        colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary)
                    )
                }
                NavigationBarItem(
                    selected = false, onClick = { navController.popBackStack() },
                    icon = { Icon(Icons.Default.ExitToApp, null) },
                    label = { Text("Salir") },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary)
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
                    colors = CardDefaults.cardColors(containerColor = BlueMain),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, tint = White, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("¡Hola, ${nombre ?: "Usuario"}!", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(getRoleLabel(rol), color = White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                }
            }

            if (rol == "ARBITRO") {
                item { Text("Partidos a arbitrar", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                if (matches.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No tienes partidos asignados", color = TextSecondary)
                            }
                        }
                    }
                } else {
                    items(matches) { match ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${match.equipo1} vs ${match.equipo2}", color = White, fontWeight = FontWeight.Bold)
                                Text("${match.fecha} - ${match.hora}", color = TextSecondary, fontSize = 13.sp)
                                Text(match.resultado, color = BlueLight, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("Acciones rápidas", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        Text("Mi Equipo", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, null, tint = BlueLight, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(myTeam?.nombre ?: equipo, color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    myTeam?.let {
                                        Text(it.deporte, color = BlueLight, fontSize = 13.sp)
                                        Text("${it.numJugadores} jugadores", color = TextSecondary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                item { Text("Mis próximas reservas", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp) }

                if (reservations.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No tienes reservas", color = TextSecondary)
                            }
                        }
                    }
                } else {
                    items(reservations.take(5)) { res ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, null, tint = BlueLight, modifier = Modifier.size(36.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(res.instalacionNombre, color = White, fontWeight = FontWeight.Bold)
                                    Text(res.fecha, color = TextSecondary, fontSize = 13.sp)
                                    Text("${res.horaInicio} - ${res.horaFin}", color = BlueLight, fontSize = 13.sp)
                                }
                                TextButton(onClick = { reservationVM.deleteReservation(res.id) }) {
                                    Text("Cancelar", color = Color(0xFFEF5350), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item { Text("Mis partidos", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                if (matches.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("No hay partidos programados", color = TextSecondary)
                            }
                        }
                    }
                } else {
                    items(matches) { match ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${match.equipo1} vs ${match.equipo2}", color = White, fontWeight = FontWeight.Bold)
                                Text("${match.fecha} - ${match.hora}", color = TextSecondary, fontSize = 13.sp)
                                Text(match.resultado, color = BlueLight, fontSize = 12.sp)
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
fun QuickAction(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp), onClick = onClick) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = BlueLight, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, color = White, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun IncidenciaCard() {
    var texto   by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null, tint = Color(0xFFFFA726))
                Spacer(Modifier.width(8.dp))
                Text("Notificar incidencia", color = White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            if (enviado) {
                Text("✓ Incidencia enviada", color = Color(0xFF4CAF50))
            } else {
                OutlinedTextField(
                    value = texto, onValueChange = { texto = it },
                    placeholder = { Text("Describe la incidencia...", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BlueLight, unfocusedBorderColor = TextSecondary, focusedTextColor = White, unfocusedTextColor = White),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { if (texto.isNotBlank()) enviado = true }, colors = ButtonDefaults.buttonColors(containerColor = BlueMain), modifier = Modifier.fillMaxWidth()) {
                    Text("Enviar", color = White)
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