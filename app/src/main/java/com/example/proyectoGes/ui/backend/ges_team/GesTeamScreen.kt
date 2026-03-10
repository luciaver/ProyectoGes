package com.example.proyectoGes.ui.backend.ges_team

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectoGes.database.AppDatabase
import com.example.proyectoGes.models.Team
import com.example.proyectoGes.models.User
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.home.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamViewModel(context: Context) : ViewModel() {

    private val teamDao = AppDatabase.getDatabase(context).teamDao()
    private val userDao = AppDatabase.getDatabase(context).userDao()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members: StateFlow<List<User>> = _members.asStateFlow()

    var teamToEdit by androidx.compose.runtime.mutableStateOf<Team?>(null)
        private set

    init {
        viewModelScope.launch { teamDao.getAll().collect { _teams.value = it } }
    }

    fun loadMembersOf(teamName: String) {
        viewModelScope.launch {
            userDao.getAll().collect { users ->
                _members.value = users.filter { it.equipo == teamName }
            }
        }
    }

    fun addTeam(team: Team) = viewModelScope.launch { teamDao.insert(team) }

    fun updateTeam(team: Team) = viewModelScope.launch { teamDao.update(team) }

    fun deleteTeam(id: Int) = viewModelScope.launch {
        teamDao.getById(id)?.let { teamDao.delete(it) }
    }

    fun getTeamById(id: Int) { teamToEdit = _teams.value.find { it.id == id } }
}

class TeamViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TeamViewModel(context) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesTeamScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: TeamViewModel = viewModel(factory = TeamViewModelFactory(context))
    val teams by vm.teams.collectAsState()
    var toDelete by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Equipos", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.AddTeam) }) {
                        Icon(Icons.Default.Add, null, tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        if (teams.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay equipos registrados", color = TextMuted)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(teams) { team ->
                    TeamCard(
                        team = team,
                        onEdit = { navController.navigate("${Routes.EditTeam}/${team.id}") },
                        onDelete = { toDelete = team }
                    )
                }
            }
        }

        toDelete?.let { t ->
            AlertDialog(
                onDismissRequest = { toDelete = null },
                title = { Text("Eliminar equipo") },
                text = { Text("¿Eliminar ${t.nombre}?") },
                confirmButton = {
                    Button(onClick = { vm.deleteTeam(t.id); toDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = DangerRed)) { Text("Eliminar") }
                },
                dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } },
                containerColor = SurfaceColor
            )
        }
    }
}

@Composable
fun TeamCard(team: Team, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Group, null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(team.nombre, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(team.deporte, color = PrimaryBlue, fontSize = 13.sp)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = PrimaryBlue) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = DangerRed) }
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
    val team = vm.teamToEdit

    if (team != null) {
        LaunchedEffect(team.nombre) { vm.loadMembersOf(team.nombre) }
        val members by vm.members.collectAsState()
        TeamFormScreen(navController = navController, title = "Editar Equipo", existing = team, members = members)
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamFormScreen(navController: NavController, title: String, existing: Team? = null, members: List<User> = emptyList()) {
    val context = LocalContext.current
    val vm: TeamViewModel = viewModel(factory = TeamViewModelFactory(context))

    var nombre  by remember { mutableStateOf(existing?.nombre ?: "") }
    var deporte by remember { mutableStateOf(existing?.deporte ?: "Pádel") }
    var error   by remember { mutableStateOf<String?>(null) }

    val deportes = listOf("Pádel", "Tenis", "Fútbol", "Baloncesto")

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text(title, color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre del equipo") }, singleLine = true,
                    colors = lightFieldColors(),
                    shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text("Deporte", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                deportes.forEach { d ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = deporte == d, onClick = { deporte = d }, colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue))
                        Text(d, color = TextPrimary)
                    }
                }
            }

            if (existing != null && members.isNotEmpty()) {
                item {
                    Text("Miembros del equipo", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
                items(members) { user ->
                    Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = PrimaryBlue)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(user.nombre, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text(if (user.rol == "JUGADOR") "Jugador · ${user.posicion ?: "-"}" else "Entrenador", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else if (existing != null) {
                item { Text("Sin miembros en este equipo", color = TextMuted) }
            }

            item { error?.let { Text(it, color = DangerRed) } }

            item {
                Button(
                    onClick = {
                        if (nombre.isBlank()) { error = "El nombre es obligatorio"; return@Button }
                        if (existing == null) vm.addTeam(Team(0, nombre, deporte))
                        else vm.updateTeam(existing.copy(nombre = nombre, deporte = deporte))
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp)
                ) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun lightFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor   = SurfaceColor,
    unfocusedContainerColor = SurfaceColor,
    focusedBorderColor      = PrimaryBlue,
    unfocusedBorderColor    = TextMuted,
    focusedTextColor        = TextPrimary,
    unfocusedTextColor      = TextPrimary,
    cursorColor             = PrimaryBlue,
    focusedLabelColor       = PrimaryBlue,
    unfocusedLabelColor     = TextMuted
)