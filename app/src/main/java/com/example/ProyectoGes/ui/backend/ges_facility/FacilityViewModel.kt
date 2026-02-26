package com.example.ProyectoGes.ui.backend.ges_facility

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ProyectoGes.database.AppDatabase
import com.example.ProyectoGes.models.Facility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FacilityViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).facilityDao()

    private val _facilities = MutableStateFlow<List<Facility>>(emptyList())
    val facilities: StateFlow<List<Facility>> = _facilities.asStateFlow()

    var facilityToEdit by mutableStateOf<Facility?>(null)
        private set

    init {
        viewModelScope.launch {
            dao.getAll().collect { _facilities.value = it }
        }
    }

    fun addFacility(facility: Facility) {
        viewModelScope.launch { dao.insert(facility) }
    }

    fun updateFacility(facility: Facility) {
        viewModelScope.launch { dao.update(facility) }
    }

    fun deleteFacility(id: Int) {
        viewModelScope.launch {
            val f = dao.getById(id)
            if (f != null) dao.delete(f)
        }
    }

    fun getFacilityById(id: Int) {
        facilityToEdit = _facilities.value.find { it.id == id }
    }
}

class FacilityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FacilityViewModel(context) as T
    }
}