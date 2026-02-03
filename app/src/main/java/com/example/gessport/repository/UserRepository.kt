package com.example.gessport.repository

import com.example.gessport.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // READ - Retornan Flow para observar cambios
    fun getAllUsers(): Flow<List<User>>
    fun getUsersByRole(rol: String): Flow<List<User>>

    // CREATE
    suspend fun addUser(user: User): User

    // UPDATE - Retorna Int (número de filas actualizadas)
    suspend fun updateUser(user: User): Int

    // DELETE - Retorna Boolean
    suspend fun deleteUser(id: Int): Boolean
}