package com.example.proyectoGes.ui.backend.ges_match

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectoGes.models.Match
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.home.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesMatchScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: MatchViewModel = viewModel(factory = MatchViewModelFactory(context))
    val matches by vm.matches.collectAsState()
    var toDelete by remember { mutableStateOf<Match?>(null) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Partidos", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.AddMatch) }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = BlueLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        if (matches.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay partidos", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(matches) { match ->
                    MatchCard(
                        match = match,
                        onEdit = { navController.navigate("${Routes.EditMatch}/${match.id}") },
                        onDelete = { toDelete = match }
                    )
                }
            }
        }

        toDelete?.let { m ->
            AlertDialog(
                onDismissRequest = { toDelete = null },
                title = { Text("Eliminar partido") },
                text = { Text("¿Eliminar ${m.equipo1} vs ${m.equipo2}?") },
                confirmButton = {
                    Button(
                        onClick = { vm.deleteMatch(m.id); toDelete = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                    ) { Text("Eliminar") }
                },
                dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } },
                containerColor = DarkSurface
            )
        }
    }
}

@Composable
fun MatchCard(match: Match, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = BlueLight, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${match.equipo1} vs ${match.equipo2}", color = White, fontWeight = FontWeight.Bold)
                Text("${match.fecha} - ${match.hora}", color = TextSecondary, fontSize = 13.sp)
                Text(match.resultado, color = BlueLight, fontSize = 12.sp)
                if (match.arbitroNombre.isNotBlank()) Text("Árbitro: ${match.arbitroNombre}", color = TextSecondary, fontSize = 12.sp)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = null, tint = BlueLight) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF5350)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMatchScreen(navController: NavController) {
    MatchFormScreen(navController = navController, title = "Nuevo Partido")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMatchScreen(navController: NavController, matchId: Int) {
    val context = LocalContext.current
    val vm: MatchViewModel = viewModel(factory = MatchViewModelFactory(context))
    LaunchedEffect(matchId) { vm.getMatchById(matchId) }
    MatchFormScreen(navController = navController, title = "Editar Partido", existing = vm.matchToEdit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFormScreen(navController: NavController, title: String, existing: Match? = null) {
    val context = LocalContext.current
    val vm: MatchViewModel = viewModel(factory = MatchViewModelFactory(context))

    var equipo1     by remember { mutableStateOf(existing?.equipo1 ?: "") }
    var equipo2     by remember { mutableStateOf(existing?.equipo2 ?: "") }
    var fecha       by remember { mutableStateOf(existing?.fecha ?: "") }
    var hora        by remember { mutableStateOf(existing?.hora ?: "") }
    var instalacion by remember { mutableStateOf(existing?.instalacionNombre ?: "") }
    var arbitro     by remember { mutableStateOf(existing?.arbitroNombre ?: "") }
    var resultado   by remember { mutableStateOf(existing?.resultado ?: "Pendiente") }
    var error       by remember { mutableStateOf<String?>(null) }

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
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MatchField("Equipo 1", equipo1) { equipo1 = it }
            MatchField("Equipo 2", equipo2) { equipo2 = it }
            MatchField("Fecha (dd/MM/yyyy)", fecha) { fecha = it }
            MatchField("Hora (HH:mm)", hora) { hora = it }
            MatchField("Instalación", instalacion) { instalacion = it }
            MatchField("Árbitro", arbitro) { arbitro = it }
            MatchField("Resultado", resultado) { resultado = it }

            error?.let { Text(it, color = Color(0xFFEF5350)) }

            Button(
                onClick = {
                    if (equipo1.isBlank() || equipo2.isBlank() || fecha.isBlank() || hora.isBlank()) {
                        error = "Equipos, fecha y hora son obligatorios"
                        return@Button
                    }
                    if (existing == null) {
                        vm.addMatch(Match(0, equipo1, equipo2, fecha, hora, instalacion, 0, arbitro, resultado))
                    } else {
                        vm.updateMatch(existing.copy(equipo1 = equipo1, equipo2 = equipo2, fecha = fecha, hora = hora, instalacionNombre = instalacion, arbitroNombre = arbitro, resultado = resultado))
                    }
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Guardar", color = White, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun MatchField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = TextSecondary) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CardColor, unfocusedContainerColor = CardColor,
            focusedBorderColor = BlueLight, unfocusedBorderColor = TextSecondary,
            focusedTextColor = White, unfocusedTextColor = White, cursorColor = BlueLight
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    )
}