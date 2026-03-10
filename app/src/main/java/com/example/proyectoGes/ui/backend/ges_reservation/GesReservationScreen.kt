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
import com.example.proyectoGes.ui.backend.ges_reservation.ReservationViewModel
import com.example.proyectoGes.ui.backend.ges_reservation.ReservationViewModelFactory

import com.example.proyectoGes.ui.backend.ges_match.DropdownSelector
import com.example.proyectoGes.ui.home.*
import java.text.SimpleDateFormat
import java.util.*

private const val PRECIO_POR_HORA = 10.0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesReservationScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val reservations by vm.reservations.collectAsState()
    var toDelete by remember { mutableStateOf<Reservation?>(null) }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Reservas", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        if (reservations.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay reservas", color = TextMuted)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reservations) { res ->
                    ReservationCard(reservation = res, onDelete = { toDelete = res })
                }
            }
        }
        toDelete?.let { r ->
            AlertDialog(
                onDismissRequest = { toDelete = null },
                title = { Text("Cancelar reserva") },
                text  = { Text("¿Cancelar reserva de ${r.userName} el ${r.fecha}?") },
                confirmButton = { Button(onClick = { vm.deleteReservation(r.id); toDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = DangerRed)) { Text("Cancelar reserva") } },
                dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Volver") } },
                containerColor = SurfaceColor
            )
        }
    }
}

@Composable
fun ReservationCard(reservation: Reservation, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, null, tint = PrimaryBlue, modifier = Modifier.size(36.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(reservation.instalacionNombre, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(reservation.userName, color = TextMuted, fontSize = 13.sp)
                Text("${reservation.fecha}  ${reservation.horaInicio} – ${reservation.horaFin}", color = PrimaryBlue, fontSize = 12.sp)
                Text("%.0f€".format(reservation.precio), color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = DangerRed) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReservationScreen(navController: NavController, userId: Int = 0, userName: String = "Usuario") {
    val context    = LocalContext.current
    val vm         = viewModel<ReservationViewModel>(factory = ReservationViewModelFactory(context))
    val facilityVm = viewModel<FacilityViewModel>(factory = FacilityViewModelFactory(context))

    val facilities           by facilityVm.facilities.collectAsState()
    val existingReservations by vm.existingReservations.collectAsState()

    val allHours = (8..22).map { h -> String.format("%02d:00", h) }

    var selectedFacilityId   by remember { mutableIntStateOf(0) }
    var selectedFacilityName by remember { mutableStateOf("") }
    var selectedDate         by remember { mutableStateOf("") }
    var selectedStart        by remember { mutableStateOf("") }
    var selectedEnd          by remember { mutableStateOf("") }
    var error                by remember { mutableStateOf<String?>(null) }
    var showCalendar         by remember { mutableStateOf(false) }

    val precio = if (selectedStart.isNotBlank() && selectedEnd.isNotBlank()) {
        val horas = (timeToMinutes(selectedEnd) - timeToMinutes(selectedStart)) / 60.0
        horas * PRECIO_POR_HORA
    } else 0.0

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

    LaunchedEffect(selectedFacilityId, selectedDate) {
        if (selectedFacilityId != 0 && selectedDate.isNotBlank()) {
            vm.loadReservationsForFacilityAndDate(selectedFacilityId, selectedDate)
            selectedStart = ""; selectedEnd = ""
        }
    }

    if (showCalendar) {
        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        selectedDate = sdf.format(Date(millis))
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

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Nueva Reserva", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

            item {
                Text("Instalación", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                DropdownSelector(
                    options = facilities.filter { it.disponible }.map { it.nombre },
                    selected = selectedFacilityName,
                    placeholder = "Seleccionar instalación"
                ) { nombre ->
                    selectedFacilityName = nombre
                    selectedFacilityId = facilities.find { it.nombre == nombre }?.id ?: 0
                }
            }

            item {
                Text("Fecha", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Button(onClick = { showCalendar = true }, colors = ButtonDefaults.buttonColors(containerColor = CardBg), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.DateRange, null, tint = PrimaryBlue)
                    Spacer(Modifier.width(8.dp))
                    Text(if (selectedDate.isBlank()) "Seleccionar fecha" else selectedDate, color = if (selectedDate.isBlank()) TextMuted else TextPrimary)
                }
            }

            if (selectedFacilityId != 0 && selectedDate.isNotBlank()) {
                item {
                    Text("Hora de inicio", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    TimeGrid(hours = allHours.dropLast(1), existingReservations = existingReservations, selectedHour = selectedStart, isStartPicker = true, otherHour = selectedEnd) { h ->
                        selectedStart = h
                        if (selectedEnd.isNotBlank() && !isValidRange(h, selectedEnd)) selectedEnd = ""
                    }
                }
                if (selectedStart.isNotBlank()) {
                    item {
                        Text("Hora de fin", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        TimeGrid(hours = allHours.filter { timeToMinutes(it) > timeToMinutes(selectedStart) }, existingReservations = existingReservations, selectedHour = selectedEnd, isStartPicker = false, otherHour = selectedStart) { selectedEnd = it }
                    }
                }
                if (selectedStart.isNotBlank() && selectedEnd.isNotBlank()) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.1f)), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EuroSymbol, null, tint = SuccessGreen)
                                Spacer(Modifier.width(8.dp))
                                Text("$selectedStart – $selectedEnd · %.0f€".format(precio), color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            item { error?.let { Text(it, color = DangerRed, fontSize = 13.sp) } }

            item {
                Button(
                    onClick = {
                        error = null
                        when {
                            selectedFacilityId == 0 -> error = "Selecciona una instalación"
                            selectedDate.isBlank()  -> error = "Selecciona una fecha"
                            selectedStart.isBlank() -> error = "Selecciona hora de inicio"
                            selectedEnd.isBlank()   -> error = "Selecciona hora de fin"
                            else -> {
                                vm.addReservationWithOverlapCheck(
                                    reservation = Reservation(0, userId, userName, selectedFacilityId, selectedFacilityName, selectedDate, selectedStart, selectedEnd, precio),
                                    onSuccess = { navController.popBackStack() },
                                    onError   = { msg -> error = msg }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp),
                    enabled = selectedStart.isNotBlank() && selectedEnd.isNotBlank()
                ) { Text("Confirmar Reserva", color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TimeGrid(hours: List<String>, existingReservations: List<Reservation>, selectedHour: String, isStartPicker: Boolean, otherHour: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        hours.chunked(3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { hour ->
                    val blocked = existingReservations.any { res ->
                        if (isStartPicker) timeToMinutes(hour) >= timeToMinutes(res.horaInicio) && timeToMinutes(hour) < timeToMinutes(res.horaFin)
                        else otherHour.isNotBlank() && timeToMinutes(otherHour) < timeToMinutes(res.horaFin) && timeToMinutes(hour) > timeToMinutes(res.horaInicio)
                    }
                    val isSelected = selectedHour == hour
                    Button(
                        onClick = { if (!blocked) onSelect(hour) }, enabled = !blocked,
                        colors = ButtonDefaults.buttonColors(containerColor = when { blocked -> Color(0xFFE5E7EB); isSelected -> PrimaryBlue; else -> CardBg }, disabledContainerColor = Color(0xFFE5E7EB)),
                        shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(vertical = 8.dp), modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(hour, color = if (blocked) TextMuted else if (isSelected) Color.White else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(if (blocked) "Ocupada" else "Libre", color = if (blocked) DangerRed else SuccessGreen, fontSize = 10.sp)
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(navController: NavController, userId: Int = 0) {
    val context = LocalContext.current
    val vm: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(context))
    val reservations by vm.reservations.collectAsState()
    LaunchedEffect(userId) { vm.loadByUser(userId) }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        }
    ) { padding ->
        if (reservations.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No tienes reservas", color = TextMuted) }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reservations) { res -> ReservationCard(reservation = res, onDelete = { vm.deleteReservation(res.id) }) }
            }
        }
    }
}

fun timeToMinutes(time: String): Int {
    val parts = time.split(":")
    return parts[0].toInt() * 60 + (parts.getOrNull(1)?.toInt() ?: 0)
}

fun isValidRange(start: String, end: String) = timeToMinutes(start) < timeToMinutes(end)