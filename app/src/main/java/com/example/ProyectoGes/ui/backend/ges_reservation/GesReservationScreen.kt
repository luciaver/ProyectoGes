package com.example.ProyectoGes.ui.backend.ges_reservation

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
import com.example.ProyectoGes.models.Reservation
import com.example.ProyectoGes.ui.home.*
import com.example.ProyectoGes.ui.backend.ges_facility.FacilityViewModel
import com.example.ProyectoGes.ui.backend.ges_facility.FacilityViewModelFactory
import Routes

// ──────────────────────────────────────────────────────────────
// LISTA DE TODAS LAS RESERVAS (Admin)
// ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesReservationScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val reservations by vm.reservations.collectAsState()

    var showDelete by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Reservation?>(null) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Reservas", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.AddReservation) }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir", tint = BlueLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        if (reservations.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay reservas registradas", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reservations) { res ->
                    ReservationAdminCard(
                        reservation = res,
                        onDelete = { toDelete = res; showDelete = true }
                    )
                }
            }
        }

        if (showDelete && toDelete != null) {
            AlertDialog(
                onDismissRequest = { showDelete = false },
                title = { Text("Cancelar reserva") },
                text = { Text("¿Cancelar reserva de ${toDelete!!.userName} el ${toDelete!!.fecha}?") },
                confirmButton = {
                    Button(
                        onClick = { vm.deleteReservation(toDelete!!.id); showDelete = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                    ) { Text("Cancelar reserva") }
                },
                dismissButton = {
                    TextButton(onClick = { showDelete = false }) { Text("Volver") }
                },
                containerColor = DarkSurface
            )
        }
    }
}

@Composable
fun ReservationAdminCard(reservation: Reservation, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = BlueLight, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(reservation.instalacionNombre, color = White, fontWeight = FontWeight.Bold)
                Text(reservation.userName, color = TextSecondary, fontSize = 13.sp)
                Text("${reservation.fecha}  ${reservation.horaInicio}-${reservation.horaFin}", color = BlueLight, fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF5350))
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// AÑADIR RESERVA (usuarios y admin)
// ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReservationScreen(navController: NavController, userId: Int = 0, userName: String = "Usuario") {
    val context = LocalContext.current
    val vm: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val facilityVm: FacilityViewModel = viewModel(factory = FacilityViewModelFactory(context))
    val facilities by facilityVm.facilities.collectAsState()

    var selectedFacilityId by remember { mutableIntStateOf(0) }
    var selectedFacilityName by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Nueva Reserva", color = White, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Instalación", color = White, fontWeight = FontWeight.SemiBold)
            facilities.forEach { facility ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedFacilityId == facility.id,
                        onClick = { selectedFacilityId = facility.id; selectedFacilityName = facility.nombre },
                        colors = RadioButtonDefaults.colors(selectedColor = BlueLight)
                    )
                    Column {
                        Text(facility.nombre, color = White)
                        Text(facility.tipo, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            AppTextField(label = "Fecha (dd/MM/yyyy)", value = fecha, onValueChange = { fecha = it })
            AppTextField(label = "Hora inicio (HH:mm)", value = horaInicio, onValueChange = { horaInicio = it })
            AppTextField(label = "Hora fin (HH:mm)", value = horaFin, onValueChange = { horaFin = it })

            error?.let { Text(it, color = Color(0xFFEF5350)) }

            Button(
                onClick = {
                    if (selectedFacilityId == 0 || fecha.isBlank() || horaInicio.isBlank() || horaFin.isBlank()) {
                        error = "Todos los campos son obligatorios"
                        return@Button
                    }
                    vm.addReservation(
                        Reservation(
                            0, userId, userName,
                            selectedFacilityId, selectedFacilityName,
                            fecha, horaInicio, horaFin
                        )
                    )
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar Reserva", color = White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────
// MIS RESERVAS (usuario)
// ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(navController: NavController, userId: Int = 0) {
    val context = LocalContext.current
    val vm: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val reservations by vm.reservations.collectAsState()

    LaunchedEffect(userId) {
        if (userId != 0) vm.loadByUser(userId)
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        if (reservations.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tienes reservas", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reservations) { res ->
                    ReservationAdminCard(
                        reservation = res,
                        onDelete = { vm.deleteReservation(res.id) }
                    )
                }
            }
        }
    }
}