package com.example.ProyectoGes.ui.backend.ges_reservation

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ProyectoGes.database.AppDatabase
import com.example.ProyectoGes.models.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).reservationDao()

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations.asStateFlow()

    var reservationToEdit by mutableStateOf<Reservation?>(null)
        private set

    init {
        viewModelScope.launch {
            dao.getAll().collect { _reservations.value = it }
        }
    }

    fun loadByUser(userId: Int) {
        viewModelScope.launch {
            dao.getByUser(userId).collect { _reservations.value = it }
        }
    }

    fun addReservation(reservation: Reservation) {
        viewModelScope.launch { dao.insert(reservation) }
    }

    fun deleteReservation(id: Int) {
        viewModelScope.launch {
            val r = dao.getById(id)
            if (r != null) dao.delete(r)
        }
    }

    fun getReservationById(id: Int) {
        reservationToEdit = _reservations.value.find { it.id == id }
    }
}

class ReservationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ReservationViewModel(context) as T
    }
}