package com.example.proyectoGes.ui.backend.ges_match

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.proyectoGes.database.AppDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MatchViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).matchDao()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    var matchToEdit by mutableStateOf<Match?>(null)
        private set

    init {
        viewModelScope.launch { dao.getAll().collect { _matches.value = it } }
    }

    fun loadByArbitro(arbitroId: Int) {
        viewModelScope.launch { dao.getByArbitro(arbitroId).collect { _matches.value = it } }
    }

    fun loadByEquipo(equipo: String) {
        viewModelScope.launch { dao.getByEquipo(equipo).collect { _matches.value = it } }
    }

    fun addMatch(match: Match) = viewModelScope.launch { dao.insert(match) }

    fun updateMatch(match: Match) = viewModelScope.launch { dao.update(match) }

    fun deleteMatch(id: Int) = viewModelScope.launch {
        dao.getById(id)?.let { dao.delete(it) }
    }

    fun getMatchById(id: Int) { matchToEdit = _matches.value.find { it.id == id } }
}

class MatchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MatchViewModel(context) as T
    }
}