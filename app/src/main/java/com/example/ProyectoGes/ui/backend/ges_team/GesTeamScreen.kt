package com.example.ProyectoGes.ui.backend.ges_team

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ProyectoGes.models.Team
import com.example.ProyectoGes.navigation.Routes


private val DarkBg        = Color(0xFF0D0D0D)
private val DarkSurface   = Color(0xFF1A1A2E)
private val CardColor     = Color(0xFF16213E)
private val BlueMain      = Color(0xFF1565C0)
private val BlueLight     = Color(0xFF2196F3)
private val White         = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFB0BEC5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesTeamScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: TeamViewModel = viewModel(factory = TeamViewModelFactory(context))
    val teams by vm.teams.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var teamToDelete by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Equipos", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.AddTeam) }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir", tint = BlueLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        if (teams.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay equipos registrados", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(teams) { team ->
                    TeamCard(
                        team = team,
                        onEdit = { navController.navigate("${Routes.EditTeam}/${team.id}") },
                        onDelete = { teamToDelete = team; showDeleteDialog = true }
                    )
                }
            }
        }

        if (showDeleteDialog && teamToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar equipo") },
                text = { Text("¿Seguro que quieres eliminar ${teamToDelete!!.nombre}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            vm.deleteTeam(teamToDelete!!.id)
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                },
                containerColor = DarkSurface
            )
        }
    }
}

@Composable
fun TeamCard(team: Team, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Group,
                contentDescription = null,
                tint = BlueLight,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(team.nombre, color = White, fontWeight = FontWeight.Bold)
                Text(team.deporte, color = BlueLight, fontSize = 13.sp)
                Text("${team.numJugadores} jugadores", color = TextSecondary, fontSize = 12.sp)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = BlueLight)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF5350))
            }
        }
    }
}

@Composable
fun AddTeamScreen(navController: NavController) {
    TeamFormScreen(navController = navController, title = "Añadir Equipo")
}

@Composable
fun EditTeamScreen(navController: NavController, teamId: Int) {
    val context = LocalContext.current
    val vm: TeamViewModel = viewModel(factory = TeamViewModelFactory(context))
    LaunchedEffect(teamId) { vm.getTeamById(teamId) }
    TeamFormScreen(navController = navController, title = "Editar Equipo", existing = vm.teamToEdit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamFormScreen(navController: NavController, title: String, existing: Team? = null) {
    val context = LocalContext.current
    val vm: TeamViewModel = viewModel(factory = TeamViewModelFactory(context))

    var nombre by remember { mutableStateOf(existing?.nombre ?: "") }
    var deporte by remember { mutableStateOf(existing?.deporte ?: "Pádel") }
    var numJugadores by remember { mutableStateOf(existing?.numJugadores?.toString() ?: "4") }
    var error by remember { mutableStateOf<String?>(null) }

    val deportes = listOf("Pádel", "Tenis", "Fútbol", "Baloncesto", "Multideporte")

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text(title, color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del equipo", color = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardColor,
                    unfocusedContainerColor = CardColor,
                    focusedBorderColor = BlueLight,
                    unfocusedBorderColor = TextSecondary,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = BlueLight
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Deporte", color = White, fontWeight = FontWeight.SemiBold)
            deportes.forEach { dep ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = deporte == dep,
                        onClick = { deporte = dep },
                        colors = RadioButtonDefaults.colors(selectedColor = BlueLight)
                    )
                    Text(dep, color = White)
                }
            }

            OutlinedTextField(
                value = numJugadores,
                onValueChange = { numJugadores = it },
                label = { Text("Nº jugadores", color = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardColor,
                    unfocusedContainerColor = CardColor,
                    focusedBorderColor = BlueLight,
                    unfocusedBorderColor = TextSecondary,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = BlueLight
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            error?.let { Text(it, color = Color(0xFFEF5350)) }

            Button(
                onClick = {
                    if (nombre.isBlank()) { error = "El nombre es obligatorio"; return@Button }
                    val num = numJugadores.toIntOrNull() ?: 0
                    if (existing == null) {
                        vm.addTeam(Team(0, nombre, deporte, 0, num))
                    } else {
                        vm.updateTeam(existing.copy(nombre = nombre, deporte = deporte, numJugadores = num))
                    }
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar", color = White, fontWeight = FontWeight.Bold)
            }
        }
    }
}