package com.example.proyectoGes.ui.backend.ges_facility

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
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.backend.ges_team.lightFieldColors
import com.example.proyectoGes.ui.home.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FacilityViewModel(context: Context) : ViewModel() {
    private val dao = AppDatabase.getDatabase(context).facilityDao()
    private val _facilities = MutableStateFlow<List<Facility>>(emptyList())
    val facilities: StateFlow<List<Facility>> = _facilities.asStateFlow()
    var facilityToEdit by mutableStateOf<Facility?>(null); private set

    init { viewModelScope.launch { dao.getAll().collect { _facilities.value = it } } }

    fun addFacility(f: Facility) = viewModelScope.launch { dao.insert(f) }
    fun updateFacility(f: Facility) = viewModelScope.launch { dao.update(f) }
    fun deleteFacility(id: Int) = viewModelScope.launch { dao.getById(id)?.let { dao.delete(it) } }
    fun getFacilityById(id: Int) { facilityToEdit = _facilities.value.find { it.id == id } }
}

class FacilityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FacilityViewModel(context) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesFacilityScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: FacilityViewModel = viewModel(factory = FacilityViewModelFactory(context))
    val facilities by vm.facilities.collectAsState()
    var toDelete by remember { mutableStateOf<Facility?>(null) }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Instalaciones", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                actions = { IconButton(onClick = { navController.navigate(Routes.AddFacility) }) { Icon(Icons.Default.Add, null, tint = PrimaryBlue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        if (facilities.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No hay instalaciones", color = TextMuted) }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(facilities) { facility ->
                    FacilityCard(facility, onEdit = { navController.navigate("${Routes.EditFacility}/${facility.id}") }, onDelete = { toDelete = facility }, onToggle = { vm.updateFacility(facility.copy(disponible = !facility.disponible)) })
                }
            }
        }
        toDelete?.let { f ->
            AlertDialog(onDismissRequest = { toDelete = null }, title = { Text("Eliminar instalación") }, text = { Text("¿Eliminar ${f.nombre}?") },
                confirmButton = { Button(onClick = { vm.deleteFacility(f.id); toDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = DangerRed)) { Text("Eliminar") } },
                dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } },
                containerColor = SurfaceColor)
        }
    }
}

@Composable
fun FacilityCard(facility: Facility, onEdit: () -> Unit, onDelete: () -> Unit, onToggle: () -> Unit) {
    val statusColor = if (facility.disponible) SuccessGreen else DangerRed
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Place, null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(facility.nombre, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(facility.tipo, color = TextMuted, fontSize = 13.sp)
                Text(if (facility.disponible) "Disponible" else "No disponible", color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Switch(checked = facility.disponible, onCheckedChange = { onToggle() }, colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue))
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = PrimaryBlue) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = DangerRed) }
        }
    }
}

@Composable
fun AddFacilityScreen(navController: NavController) { FacilityFormScreen(navController, "Nueva Instalación") }

@Composable
fun EditFacilityScreen(navController: NavController, facilityId: Int) {
    val context = LocalContext.current
    val vm: FacilityViewModel = viewModel(factory = FacilityViewModelFactory(context))
    LaunchedEffect(facilityId) { vm.getFacilityById(facilityId) }
    FacilityFormScreen(navController, "Editar Instalación", vm.facilityToEdit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityFormScreen(navController: NavController, title: String, existing: Facility? = null) {
    val context = LocalContext.current
    val vm: FacilityViewModel = viewModel(factory = FacilityViewModelFactory(context))
    var nombre     by remember { mutableStateOf(existing?.nombre ?: "") }
    var tipo       by remember { mutableStateOf(existing?.tipo ?: "Pádel") }
    var disponible by remember { mutableStateOf(existing?.disponible ?: true) }
    var error      by remember { mutableStateOf<String?>(null) }
    val tipos = listOf("Pádel", "Tenis", "Fútbol", "Baloncesto")

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(title = { Text(title, color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor))
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, colors = lightFieldColors(), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
            Text("Tipo", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            tipos.forEach { t ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipo == t, onClick = { tipo = t }, colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue))
                    Text(t, color = TextPrimary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Disponible", color = TextPrimary)
                Spacer(Modifier.width(12.dp))
                Switch(checked = disponible, onCheckedChange = { disponible = it }, colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue))
            }
            error?.let { Text(it, color = DangerRed) }
            Button(
                onClick = {
                    if (nombre.isBlank()) { error = "El nombre es obligatorio"; return@Button }
                    if (existing == null) vm.addFacility(Facility(0, nombre, tipo, disponible))
                    else vm.updateFacility(existing.copy(nombre = nombre, tipo = tipo, disponible = disponible))
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp)
            ) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}