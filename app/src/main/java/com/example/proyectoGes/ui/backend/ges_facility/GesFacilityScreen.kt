package com.example.proyectoGes.ui.backend.ges_facility

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
import com.example.proyectoGes.models.Facility
import com.example.proyectoGes.navigation.Routes
import com.example.proyectoGes.ui.home.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesFacilityScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: FacilityViewModel = viewModel(factory = FacilityViewModelFactory(context))
    val facilities by vm.facilities.collectAsState()
    var toDelete by remember { mutableStateOf<Facility?>(null) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Instalaciones", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.AddFacility) }) {
                        Icon(Icons.Default.Add, null, tint = BlueLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        if (facilities.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay instalaciones", color = TextSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(facilities) { facility ->
                    FacilityCard(
                        facility = facility,
                        onEdit = { navController.navigate("${Routes.EditFacility}/${facility.id}") },
                        onDelete = { toDelete = facility },
                        onToggle = { vm.updateFacility(facility.copy(disponible = !facility.disponible)) }
                    )
                }
            }
        }

        toDelete?.let { f ->
            AlertDialog(
                onDismissRequest = { toDelete = null },
                title = { Text("Eliminar instalación") },
                text = { Text("¿Eliminar ${f.nombre}?") },
                confirmButton = {
                    Button(onClick = { vm.deleteFacility(f.id); toDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))) { Text("Eliminar") }
                },
                dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } },
                containerColor = DarkSurface
            )
        }
    }
}

@Composable
fun FacilityCard(facility: Facility, onEdit: () -> Unit, onDelete: () -> Unit, onToggle: () -> Unit) {
    val statusColor = if (facility.disponible) Color(0xFF4CAF50) else Color(0xFFEF5350)
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardColor), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Place, null, tint = BlueLight, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(facility.nombre, color = White, fontWeight = FontWeight.Bold)
                Text(facility.tipo, color = TextSecondary, fontSize = 13.sp)
                Text(if (facility.disponible) "Disponible" else "No disponible", color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Switch(checked = facility.disponible, onCheckedChange = { onToggle() }, colors = SwitchDefaults.colors(checkedThumbColor = BlueLight))
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = BlueLight) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color(0xFFEF5350)) }
        }
    }
}

@Composable
fun AddFacilityScreen(navController: NavController) {
    FacilityFormScreen(navController = navController, title = "Nueva Instalación")
}

@Composable
fun EditFacilityScreen(navController: NavController, facilityId: Int) {
    val context = LocalContext.current
    val vm: FacilityViewModel = viewModel(factory = FacilityViewModelFactory(context))
    LaunchedEffect(facilityId) { vm.getFacilityById(facilityId) }
    FacilityFormScreen(navController = navController, title = "Editar Instalación", existing = vm.facilityToEdit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityFormScreen(navController: NavController, title: String, existing: Facility? = null) {
    val context = LocalContext.current
    val vm: FacilityViewModel = viewModel(factory = FacilityViewModelFactory(context))

    var nombre     by remember { mutableStateOf(existing?.nombre ?: "") }
    var tipo       by remember { mutableStateOf(existing?.tipo ?: "Pádel") }
    var capacidad  by remember { mutableStateOf(existing?.capacidad?.toString() ?: "4") }
    var disponible by remember { mutableStateOf(existing?.disponible ?: true) }
    var error      by remember { mutableStateOf<String?>(null) }

    val tipos = listOf("Pádel", "Tenis", "Fútbol", "Baloncesto")

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text(title, color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre", color = TextSecondary) }, singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardColor, unfocusedContainerColor = CardColor,
                    focusedBorderColor = BlueLight, unfocusedBorderColor = TextSecondary,
                    focusedTextColor = White, unfocusedTextColor = White, cursorColor = BlueLight
                ),
                shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
            )

            Text("Tipo", color = White, fontWeight = FontWeight.SemiBold)
            tipos.forEach { t ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipo == t, onClick = { tipo = t }, colors = RadioButtonDefaults.colors(selectedColor = BlueLight))
                    Text(t, color = White)
                }
            }

            OutlinedTextField(
                value = capacidad, onValueChange = { capacidad = it },
                label = { Text("Capacidad", color = TextSecondary) }, singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardColor, unfocusedContainerColor = CardColor,
                    focusedBorderColor = BlueLight, unfocusedBorderColor = TextSecondary,
                    focusedTextColor = White, unfocusedTextColor = White, cursorColor = BlueLight
                ),
                shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Disponible", color = White)
                Spacer(Modifier.width(12.dp))
                Switch(checked = disponible, onCheckedChange = { disponible = it }, colors = SwitchDefaults.colors(checkedThumbColor = BlueLight))
            }

            error?.let { Text(it, color = Color(0xFFEF5350)) }

            Button(
                onClick = {
                    if (nombre.isBlank()) { error = "El nombre es obligatorio"; return@Button }
                    val cap = capacidad.toIntOrNull() ?: 4
                    if (existing == null) vm.addFacility(Facility(0, nombre, tipo, disponible, cap))
                    else vm.updateFacility(existing.copy(nombre = nombre, tipo = tipo, disponible = disponible, capacidad = cap))
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp)
            ) { Text("Guardar", color = White, fontWeight = FontWeight.Bold) }
        }
    }
}