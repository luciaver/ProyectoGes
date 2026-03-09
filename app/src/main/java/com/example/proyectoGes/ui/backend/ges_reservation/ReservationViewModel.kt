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

    // Horas ya reservadas para la instalación + fecha seleccionadas
    private val _bookedSlots = MutableStateFlow<List<String>>(emptyList())
    val bookedSlots: StateFlow<List<String>> = _bookedSlots.asStateFlow()

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

    fun loadBookedSlots(facilityId: Int, fecha: String) {
        viewModelScope.launch {
            _bookedSlots.value = dao.getBookedSlots(facilityId, fecha)
        }
    }

    fun addReservation(reservation: Reservation) {
        viewModelScope.launch { dao.insert(reservation) }
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