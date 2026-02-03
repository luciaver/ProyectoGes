package com.example.gessport.ui.backend.ges_user

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gessport.data.RoomUserRepository
import com.example.gessport.database.AppDatabase
import com.example.gessport.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GesUserViewModel(context: Context) : ViewModel() {

    private val userRepository = RoomUserRepository(
        AppDatabase.getDatabase(context).userDao()
    )

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private var _selectedRole by mutableStateOf<String?>(null)
    val selectedRole: String? get() = _selectedRole

    private var _userToEdit by mutableStateOf<User?>(null)
    val userToEdit: User? get() = _userToEdit

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            // Cancelar colección anterior si existe
            val flow = if (_selectedRole == null) {
                userRepository.getAllUsers()
            } else {
                userRepository.getUsersByRole(_selectedRole!!)
            }

            flow.collect { userList ->
                _users.value = userList
            }
        }
    }

    fun onRoleSelected(rol: String?) {
        _selectedRole = rol
        loadUsers() // Recargar con el nuevo filtro
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            userRepository.addUser(user)
            // El Flow actualizará automáticamente la lista
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            val rowsUpdated = userRepository.updateUser(user)
            // rowsUpdated contendrá el número de filas actualizadas
            // El Flow actualizará automáticamente la lista
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            val success = userRepository.deleteUser(id)
            // El Flow actualizará automáticamente la lista
        }
    }

    fun getUserById(id: Int) {
        // Buscar en la lista actual cargada por el Flow
        _userToEdit = _users.value.find { it.id == id }
    }
}