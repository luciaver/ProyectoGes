package com.example.gessport.data

import com.example.gessport.database.UserDao
import com.example.gessport.models.User
import com.example.gessport.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class RoomUserRepository(private val userDao: UserDao) : UserRepository {

    // READ - Retornan Flow directamente desde el DAO
    override fun getAllUsers(): Flow<List<User>> = userDao.getAll()

    override fun getUsersByRole(rol: String): Flow<List<User>> =
        userDao.getByRole(rol)

    // CREATE - Inserta y retorna el usuario con el ID generado
    override suspend fun addUser(user: User): User {
        val id = userDao.insert(user)
        return user.copy(id = id.toInt())
    }

    // UPDATE - Retorna el número de filas actualizadas (Int)
    override suspend fun updateUser(user: User): Int {
        return userDao.update(user)
    }

    // DELETE - Retorna Boolean indicando éxito
    override suspend fun deleteUser(id: Int): Boolean {
        val user = userDao.getById(id)
        return if (user != null) {
            userDao.delete(user)
            true
        } else {
            false
        }
    }

    // Método adicional para login (no está en la interface)
    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }
}