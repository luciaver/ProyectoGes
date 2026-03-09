package com.example.proyectoGes.ui.backend.ges_team

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectoGes.database.AppDatabase
import com.example.proyectoGes.models.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamViewModel(context: Context) : ViewModel() {

    private val dao = AppDatabase.getDatabase(context).teamDao()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    var teamToEdit by mutableStateOf<Team?>(null)
        private set

    init {
        viewModelScope.launch {
            dao.getAll().collect { _teams.value = it }
        }
    }

    fun addTeam(team: Team) {
        viewModelScope.launch { dao.insert(team) }
    }

    fun updateTeam(team: Team) {
        viewModelScope.launch { dao.update(team) }
    }

    fun deleteTeam(id: Int) {
        viewModelScope.launch {
            val team = dao.getById(id)
            if (team != null) dao.delete(team)
        }
    }

    fun getTeamById(id: Int) {
        teamToEdit = _teams.value.find { it.id == id }
    }
}

class TeamViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TeamViewModel(context) as T
    }
}