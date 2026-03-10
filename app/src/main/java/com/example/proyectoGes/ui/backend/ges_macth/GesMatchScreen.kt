package com.example.proyectoGes.ui.backend.ges_match

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
import com.example.proyectoGes.models.Facility
import com.example.proyectoGes.models.Match
import com.example.proyectoGes.models.Team
import com.example.proyectoGes.models.User
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.backend.ges_team.lightFieldColors
import com.example.proyectoGes.ui.home.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MatchViewModel(context: Context) : ViewModel() {
    private val matchDao    = AppDatabase.getDatabase(context).matchDao()
    private val teamDao     = AppDatabase.getDatabase(context).teamDao()
    private val facilityDao = AppDatabase.getDatabase(context).facilityDao()
    private val userDao     = AppDatabase.getDatabase(context).userDao()

    private val _matches    = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()
    private val _teams      = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()
    private val _facilities = MutableStateFlow<List<Facility>>(emptyList())
    val facilities: StateFlow<List<Facility>> = _facilities.asStateFlow()
    private val _arbitros   = MutableStateFlow<List<User>>(emptyList())
    val arbitros: StateFlow<List<User>> = _arbitros.asStateFlow()

    var matchToEdit by androidx.compose.runtime.mutableStateOf<Match?>(null); private set

    init {
        viewModelScope.launch { matchDao.getAll().collect { _matches.value = it } }
        viewModelScope.launch { teamDao.getAll().collect { _teams.value = it } }
        viewModelScope.launch { facilityDao.getAll().collect { _facilities.value = it } }
        viewModelScope.launch { userDao.getByRole("ARBITRO").collect { _arbitros.value = it } }
    }

    fun loadByArbitro(id: Int) = viewModelScope.launch { matchDao.getByArbitro(id).collect { _matches.value = it } }
    fun loadByEquipo(equipo: String) = viewModelScope.launch { matchDao.getByEquipo(equipo).collect { _matches.value = it } }
    fun addMatch(match: Match) = viewModelScope.launch { matchDao.insert(match) }
    fun updateMatch(match: Match) = viewModelScope.launch { matchDao.update(match) }
    fun deleteMatch(id: Int) = viewModelScope.launch { matchDao.getById(id)?.let { matchDao.delete(it) } }
    fun getMatchById(id: Int) { matchToEdit = _matches.value.find { it.id == id } }
}

class MatchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return MatchViewModel(context) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesMatchScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: MatchViewModel = viewModel(factory = MatchViewModelFactory(context))
    val matches by vm.matches.collectAsState()
    var toDelete by remember { mutableStateOf<Match?>(null) }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Partidos", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                actions = { IconButton(onClick = { navController.navigate(Routes.AddMatch) }) { Icon(Icons.Default.Add, null, tint = PrimaryBlue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        if (matches.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No hay partidos", color = TextMuted) }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(matches) { match ->
                    MatchCard(match, onEdit = { navController.navigate("${Routes.EditMatch}/${match.id}") }, onDelete = { toDelete = match })
                }
            }
        }
        toDelete?.let { m ->
            AlertDialog(onDismissRequest = { toDelete = null }, title = { Text("Eliminar partido") }, text = { Text("¿Eliminar ${m.equipo1} vs ${m.equipo2}?") },
                confirmButton = { Button(onClick = { vm.deleteMatch(m.id); toDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = DangerRed)) { Text("Eliminar") } },
                dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } },
                containerColor = SurfaceColor)
        }
    }
}

@Composable
fun MatchCard(match: Match, onEdit: () -> Unit, onDelete: () -> Unit) {
    val estadoColor = when (match.resultado) {
        "En curso"   -> Color(0xFFF59E0B)
        "Finalizado" -> SuccessGreen
        else         -> TextMuted
    }
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.SportsSoccer, null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${match.equipo1} vs ${match.equipo2}", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("${match.fecha} - ${match.hora}", color = TextMuted, fontSize = 13.sp)
                Text(match.resultado, color = estadoColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                if (match.arbitroNombre.isNotBlank()) Text("Árbitro: ${match.arbitroNombre}", color = TextMuted, fontSize = 12.sp)
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = PrimaryBlue) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = DangerRed) }
        }
    }
}

@Composable
fun AddMatchScreen(navController: NavController) { MatchFormScreen(navController, "Nuevo Partido") }

@Composable
fun EditMatchScreen(navController: NavController, matchId: Int) {
    val context = LocalContext.current
    val vm: MatchViewModel = viewModel(factory = MatchViewModelFactory(context))
    LaunchedEffect(matchId) { vm.getMatchById(matchId) }
    MatchFormScreen(navController, "Editar Partido", vm.matchToEdit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFormScreen(navController: NavController, title: String, existing: Match? = null) {
    val context = LocalContext.current
    val vm: MatchViewModel = viewModel(factory = MatchViewModelFactory(context))
    val teams      by vm.teams.collectAsState()
    val facilities by vm.facilities.collectAsState()
    val arbitros   by vm.arbitros.collectAsState()

    var equipo1       by remember { mutableStateOf(existing?.equipo1 ?: "") }
    var equipo2       by remember { mutableStateOf(existing?.equipo2 ?: "") }
    var fecha         by remember { mutableStateOf(existing?.fecha ?: "") }
    var hora          by remember { mutableStateOf(existing?.hora ?: "") }
    var instalacion   by remember { mutableStateOf(existing?.instalacionNombre ?: "") }
    var arbitroNombre by remember { mutableStateOf(existing?.arbitroNombre ?: "") }
    var arbitroId     by remember { mutableIntStateOf(existing?.arbitroId ?: 0) }
    var resultado     by remember { mutableStateOf(existing?.resultado ?: "Pendiente") }
    var error         by remember { mutableStateOf<String?>(null) }
    var showCalendar  by remember { mutableStateOf(false) }
    var showTime      by remember { mutableStateOf(false) }

    val dateState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                return utcTimeMillis >= cal.timeInMillis
            }
        }
    )
    val timeState = rememberTimePickerState(initialHour = 10, initialMinute = 0, is24Hour = true)

    if (showCalendar) {
        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        fecha = sdf.format(Date(millis))
                    }
                    showCalendar = false
                }) { Text("OK", color = PrimaryBlue) }
            },
            dismissButton = { TextButton(onClick = { showCalendar = false }) { Text("Cancelar") } },
            colors = DatePickerDefaults.colors(containerColor = SurfaceColor)
        ) {
            DatePicker(state = dateState, colors = DatePickerDefaults.colors(
                containerColor = SurfaceColor, selectedDayContainerColor = PrimaryBlue,
                selectedDayContentColor = Color.White, todayDateBorderColor = PrimaryBlue, todayContentColor = PrimaryBlue
            ))
        }
    }

    if (showTime) {
        AlertDialog(
            onDismissRequest = { showTime = false },
            title = { Text("Seleccionar hora") },
            text = {
                TimePicker(state = timeState, colors = TimePickerDefaults.colors(
                    clockDialColor = CardBg, selectorColor = PrimaryBlue,
                    timeSelectorSelectedContainerColor = PrimaryBlue
                ))
            },
            confirmButton = {
                TextButton(onClick = {
                    hora = String.format("%02d:%02d", timeState.hour, timeState.minute)
                    showTime = false
                }) { Text("OK", color = PrimaryBlue) }
            },
            dismissButton = { TextButton(onClick = { showTime = false }) { Text("Cancelar") } },
            containerColor = SurfaceColor
        )
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text(title, color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            item {
                FormLabel("Equipo local")
                DropdownSelector(options = teams.map { it.nombre }, selected = equipo1, placeholder = "Seleccionar equipo") {
                    equipo1 = it; if (it == equipo2) equipo2 = ""
                }
            }

            item {
                FormLabel("Equipo visitante")
                DropdownSelector(options = teams.map { it.nombre }.filter { it != equipo1 }, selected = equipo2, placeholder = "Seleccionar equipo") { equipo2 = it }
            }

            item {
                FormLabel("Fecha")
                Button(onClick = { showCalendar = true }, colors = ButtonDefaults.buttonColors(containerColor = CardBg), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.DateRange, null, tint = PrimaryBlue)
                    Spacer(Modifier.width(8.dp))
                    Text(if (fecha.isBlank()) "Seleccionar fecha" else fecha, color = if (fecha.isBlank()) TextMuted else TextPrimary)
                }
            }

            item {
                FormLabel("Hora")
                Button(onClick = { showTime = true }, colors = ButtonDefaults.buttonColors(containerColor = CardBg), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Schedule, null, tint = PrimaryBlue)
                    Spacer(Modifier.width(8.dp))
                    Text(if (hora.isBlank()) "Seleccionar hora" else hora, color = if (hora.isBlank()) TextMuted else TextPrimary)
                }
            }

            item {
                FormLabel("Instalación")
                DropdownSelector(options = facilities.map { it.nombre }, selected = instalacion, placeholder = "Seleccionar instalación") { instalacion = it }
            }

            item {
                FormLabel("Árbitro")
                DropdownSelector(options = arbitros.map { it.nombre }, selected = arbitroNombre, placeholder = "Seleccionar árbitro") { nombre ->
                    arbitroNombre = nombre
                    arbitroId = arbitros.find { it.nombre == nombre }?.id ?: 0
                }
            }

            item {
                FormLabel("Resultado")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Pendiente", "En curso", "Finalizado").forEach { r ->
                        FilterChip(selected = resultado == r, onClick = { resultado = r }, label = { Text(r) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryBlue, selectedLabelColor = Color.White))
                    }
                }
            }

            item { error?.let { Text(it, color = DangerRed) } }

            item {
                Button(
                    onClick = {
                        when {
                            equipo1.isBlank() || equipo2.isBlank() -> error = "Selecciona ambos equipos"
                            equipo1 == equipo2 -> error = "Un equipo no puede jugar contra sí mismo"
                            fecha.isBlank() -> error = "Selecciona una fecha"
                            hora.isBlank()  -> error = "Selecciona una hora"
                            else -> {
                                val match = Match(existing?.id ?: 0, equipo1, equipo2, fecha, hora, instalacion, arbitroId, arbitroNombre, resultado)
                                if (existing == null) vm.addMatch(match) else vm.updateMatch(match)
                                navController.popBackStack()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp)
                ) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(text, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(4.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(options: List<String>, selected: String, placeholder: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected.ifBlank { "" }, onValueChange = {}, readOnly = true,
            placeholder = { Text(placeholder, color = TextMuted) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = lightFieldColors(), shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.exposedDropdownSize()) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt, color = TextPrimary) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}