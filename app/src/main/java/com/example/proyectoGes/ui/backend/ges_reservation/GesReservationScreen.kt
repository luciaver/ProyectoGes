package com.example.proyectoGes.ui.backend.ges_reservation

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
import com.example.proyectoGes.models.Reservation
import com.example.proyectoGes.ui.backend.ges_facility.FacilityViewModel
import com.example.proyectoGes.ui.backend.ges_facility.FacilityViewModelFactory
import com.example.proyectoGes.ui.home.*
import java.text.SimpleDateFormat
import java.util.*

// ──────────────────────────────────────────────────────────────────────────────
// GESTIÓN DE RESERVAS — solo admin: lista + eliminar (sin botón añadir)
// ──────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesReservationScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val reservations by vm.reservations.collectAsState()

    var showDelete by remember { mutableStateOf(false) }
    var toDelete   by remember { mutableStateOf<Reservation?>(null) }

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
                // Admin NO tiene botón "+" aquí
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
                        onDelete    = { toDelete = res; showDelete = true }
                    )
                }
            }
        }

        if (showDelete && toDelete != null) {
            AlertDialog(
                onDismissRequest = { showDelete = false },
                title = { Text("Cancelar reserva") },
                text  = {
                    Text("¿Cancelar reserva de ${toDelete!!.userName} el ${toDelete!!.fecha} a las ${toDelete!!.horaInicio}?")
                },
                confirmButton = {
                    Button(
                        onClick = { vm.deleteReservation(toDelete!!.id); showDelete = false },
                        colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
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
        colors   = CardDefaults.cardColors(containerColor = CardColor),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = BlueLight, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(reservation.instalacionNombre, color = White, fontWeight = FontWeight.Bold)
                Text(reservation.userName, color = TextSecondary, fontSize = 13.sp)
                Text(
                    "${reservation.fecha}  ${reservation.horaInicio} – ${reservation.horaFin}",
                    color = BlueLight, fontSize = 12.sp
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF5350))
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// AÑADIR RESERVA — con calendario y franja horaria
// ──────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReservationScreen(
    navController: NavController,
    userId: Int = 0,
    userName: String = "Usuario"
) {
    val context    = LocalContext.current
    val vm: ReservationViewModel  = viewModel(factory = ReservationViewModelFactory(context))
    val facilityVm: FacilityViewModel = viewModel(factory = FacilityViewModelFactory(context))

    val facilities  by facilityVm.facilities.collectAsState()
    val bookedSlots by vm.bookedSlots.collectAsState()

    // Estado de selección
    var selectedFacilityId   by remember { mutableIntStateOf(0) }
    var selectedFacilityName by remember { mutableStateOf("") }
    var selectedDate         by remember { mutableStateOf("") }     // "dd/MM/yyyy"
    var selectedSlot         by remember { mutableStateOf("") }     // "HH:00"
    var error                by remember { mutableStateOf<String?>(null) }

    // Calendario
    var showCalendar         by remember { mutableStateOf(false) }
    val datePickerState      = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Solo fechas desde hoy en adelante
                return utcTimeMillis >= System.currentTimeMillis() - 86_400_000L
            }
        }
    )

    val timeSlots = (10..21).map { h -> String.format("%02d:00", h) }

    // Recargar slots al cambiar instalación o fecha
    LaunchedEffect(selectedFacilityId, selectedDate) {
        if (selectedFacilityId != 0 && selectedDate.isNotBlank()) {
            vm.loadBookedSlots(selectedFacilityId, selectedDate)
            selectedSlot = "" // reset slot al cambiar fecha/pista
        }
    }

    // Diálogo de calendario
    if (showCalendar) {
        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        selectedDate = sdf.format(Date(millis))
                    }
                    showCalendar = false
                }) { Text("OK", color = BlueLight) }
            },
            dismissButton = {
                TextButton(onClick = { showCalendar = false }) { Text("Cancelar", color = TextSecondary) }
            },
            colors = DatePickerDefaults.colors(containerColor = DarkSurface)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor          = DarkSurface,
                    titleContentColor       = White,
                    headlineContentColor    = White,
                    weekdayContentColor     = TextSecondary,
                    selectedDayContainerColor = BlueMain,
                    selectedDayContentColor   = White,
                    todayContentColor         = BlueLight,
                    todayDateBorderColor      = BlueLight
                )
            )
        }
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Seleccionar instalación ───────────────────────────────────
            item {
                SectionTitle("Instalación")
                facilities.forEach { facility ->
                    val isAvailable = facility.disponible
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected  = selectedFacilityId == facility.id,
                            onClick   = {
                                if (isAvailable) {
                                    selectedFacilityId   = facility.id
                                    selectedFacilityName = facility.nombre
                                }
                            },
                            enabled = isAvailable,
                            colors  = RadioButtonDefaults.colors(selectedColor = BlueLight)
                        )
                        Column {
                            Text(
                                facility.nombre,
                                color    = if (isAvailable) White else TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                facility.tipo,
                                color    = TextSecondary,
                                fontSize = 12.sp
                            )
                            if (!isAvailable) {
                                Text(
                                    "No disponible",
                                    color    = Color(0xFFEF5350),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // ── Seleccionar fecha con calendario ──────────────────────────
            item {
                SectionTitle("Fecha")
                Button(
                    onClick  = { showCalendar = true },
                    colors   = ButtonDefaults.buttonColors(containerColor = CardColor),
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = BlueLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text  = if (selectedDate.isBlank()) "Seleccionar fecha" else selectedDate,
                        color = if (selectedDate.isBlank()) TextSecondary else White,
                        fontSize = 15.sp
                    )
                }
            }

            // ── Franjas horarias ──────────────────────────────────────────
            if (selectedFacilityId != 0 && selectedDate.isNotBlank()) {
                item {
                    SectionTitle("Hora (10:00 – 22:00, franjas de 1 hora)")
                    Spacer(modifier = Modifier.height(4.dp))

                    // Grid 3 columnas
                    val rows = timeSlots.chunked(3)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        rows.forEach { rowSlots ->
                            Row(
                                modifier            = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowSlots.forEach { slot ->
                                    val taken    = bookedSlots.contains(slot)
                                    val selected = selectedSlot == slot
                                    val bgColor = when {
                                        taken    -> Color(0xFF37474F)   // gris: ocupada
                                        selected -> BlueMain            // azul: seleccionada
                                        else     -> CardColor           // oscuro: libre
                                    }
                                    val textColor = when {
                                        taken    -> TextSecondary
                                        selected -> White
                                        else     -> White
                                    }
                                    Button(
                                        onClick  = { if (!taken) selectedSlot = slot },
                                        enabled  = !taken,
                                        colors   = ButtonDefaults.buttonColors(
                                            containerColor         = bgColor,
                                            disabledContainerColor = bgColor
                                        ),
                                        shape    = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(slot, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                if (taken) "Ocupada" else "Libre",
                                                color    = if (taken) Color(0xFFEF5350) else Color(0xFF4CAF50),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                                // Relleno si la fila tiene menos de 3
                                repeat(3 - rowSlots.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // ── Error ─────────────────────────────────────────────────────
            item {
                error?.let {
                    Text(it, color = Color(0xFFEF5350), fontSize = 13.sp)
                }
            }

            // ── Confirmar ─────────────────────────────────────────────────
            item {
                Button(
                    onClick = {
                        when {
                            selectedFacilityId == 0 ->
                                error = "Selecciona una instalación"
                            selectedDate.isBlank() ->
                                error = "Selecciona una fecha"
                            selectedSlot.isBlank() ->
                                error = "Selecciona una franja horaria"
                            else -> {
                                val horaFin = calcularHoraFin(selectedSlot)
                                vm.addReservation(
                                    Reservation(
                                        id                = 0,
                                        userId            = userId,
                                        userName          = userName,
                                        instalacionId     = selectedFacilityId,
                                        instalacionNombre = selectedFacilityName,
                                        fecha             = selectedDate,
                                        horaInicio        = selectedSlot,
                                        horaFin           = horaFin
                                    )
                                )
                                navController.popBackStack()
                            }
                        }
                    },
                    colors   = ButtonDefaults.buttonColors(containerColor = BlueMain),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar Reserva", color = White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// MIS RESERVAS — solo las del usuario que ha iniciado sesión
// ──────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(navController: NavController, userId: Int = 0) {
    val context = LocalContext.current
    val vm: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val reservations by vm.reservations.collectAsState()

    LaunchedEffect(userId) {
        vm.loadByUser(userId)
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
                        onDelete    = { vm.deleteReservation(res.id) }
                    )
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/** Calcula la hora de fin sumando 1 hora. Ej: "10:00" → "11:00" */
private fun calcularHoraFin(horaInicio: String): String {
    val parts = horaInicio.split(":")
    val hora  = parts[0].toInt()
    return String.format("%02d:00", hora + 1)
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    Spacer(modifier = Modifier.height(6.dp))
}