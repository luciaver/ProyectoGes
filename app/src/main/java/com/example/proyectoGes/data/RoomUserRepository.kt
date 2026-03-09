package com.example.proyectoGes.data

import com.example.proyectoGes.database.UserDao
import com.example.proyectoGes.models.User
import com.example.proyectoGes.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class RoomUserRepository(private val userDao: UserDao) : UserRepository {

    override fun getAllUsers(): Flow<List<User>> = userDao.getAll()

    override fun getUsersByRole(rol: String): Flow<List<User>> = userDao.getByRole(rol)

    override suspend fun addUser(user: User): User {
        val id = userDao.insert(user)
        return user.copy(id = id.toInt())
    }

    override suspend fun updateUser(user: User): Int = userDao.update(user)

    override suspend fun deleteUser(id: Int): Boolean {
        val user = userDao.getById(id) ?: return false
        userDao.delete(user)
        return true
    }

    suspend fun getUserById(id: Int): User? = userDao.getById(id)

    suspend fun login(email: String, password: String): User? = userDao.login(email, password)
}