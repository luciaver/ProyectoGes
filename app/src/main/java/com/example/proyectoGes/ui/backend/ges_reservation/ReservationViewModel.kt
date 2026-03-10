package com.example.proyectoGes.ui.backend.ges_reservation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectoGes.database.AppDatabase
import com.example.proyectoGes.models.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).reservationDao()

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations.asStateFlow()

    /** Lista de reservas existentes para la instalación+fecha seleccionadas */
    private val _existingReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val existingReservations: StateFlow<List<Reservation>> = _existingReservations.asStateFlow()

    /** Resultado de la última comprobación de solapamiento */
    private val _overlapError = MutableStateFlow<String?>(null)
    val overlapError: StateFlow<String?> = _overlapError.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAll().collect { _reservations.value = it }
        }
    }

    /** Carga solo las reservas del usuario indicado. */
    fun loadByUser(userId: Int) {
        viewModelScope.launch {
            dao.getByUser(userId).collect { _reservations.value = it }
        }
    }

    /** Carga las reservas existentes para mostrar franjas ocupadas */
    fun loadReservationsForFacilityAndDate(facilityId: Int, fecha: String) {
        viewModelScope.launch {
            _existingReservations.value = dao.getByFacilityAndDate(facilityId, fecha)
        }
    }

    /**
     * Comprueba solapamiento y guarda la reserva si no hay conflicto.
     * Llama a [onSuccess] si se guardó, o a [onError] con el mensaje de error.
     */
    fun addReservationWithOverlapCheck(
        reservation: Reservation,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val overlaps = dao.getOverlapping(
                facilityId = reservation.instalacionId,
                fecha      = reservation.fecha,
                horaInicio = reservation.horaInicio,
                horaFin    = reservation.horaFin,
                excludeId  = 0
            )
            if (overlaps.isNotEmpty()) {
                val conflicto = overlaps.first()
                onError("Horario ocupado: ${conflicto.userName} reservó de ${conflicto.horaInicio} a ${conflicto.horaFin}")
            } else {
                dao.insert(reservation)
                onSuccess()
            }
        }
    }

    fun deleteReservation(id: Int) {
        viewModelScope.launch {
            val r = dao.getById(id) ?: return@launch
            dao.delete(r)
        }
    }
}

class ReservationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ReservationViewModel(context) as T
    }
}