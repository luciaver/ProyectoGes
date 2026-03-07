package com.example.ProyectoGes.ui.backend.ges_user

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ProyectoGes.data.RoomUserRepository
import com.example.ProyectoGes.database.AppDatabase
import com.example.ProyectoGes.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GesUserViewModel(context: Context) : ViewModel() {

    private val userRepository = RoomUserRepository(AppDatabase.getDatabase(context).userDao())

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private var _selectedRole by mutableStateOf<String?>(null)
    val selectedRole: String? get() = _selectedRole

    private var _userToEdit by mutableStateOf<User?>(null)
    val userToEdit: User? get() = _userToEdit

    init { loadUsers() }

    private fun loadUsers() {
        viewModelScope.launch {
            val flow = if (_selectedRole == null) userRepository.getAllUsers()
            else userRepository.getUsersByRole(_selectedRole!!)
            flow.collect { _users.value = it }
        }
    }

    fun onRoleSelected(rol: String?) {
        _selectedRole = rol
        loadUsers()
    }

    fun addUser(user: User) {
        viewModelScope.launch { userRepository.addUser(user) }
    }

    fun updateUser(user: User) {
        viewModelScope.launch { userRepository.updateUser(user) }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch { userRepository.deleteUser(id) }
    }

    fun getUserById(id: Int) {
        _userToEdit = _users.value.find { it.id == id }
    }
}