package com.example.ProyectoGes.data

import com.example.ProyectoGes.database.UserDao
import com.example.ProyectoGes.models.User
import com.example.ProyectoGes.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class RoomUserRepository(private val userDao: UserDao) : UserRepository {


    override fun getAllUsers(): Flow<List<User>> = userDao.getAll()

    override fun getUsersByRole(rol: String): Flow<List<User>> =
        userDao.getByRole(rol)


    override suspend fun addUser(user: User): User {
        val id = userDao.insert(user)
        return user.copy(id = id.toInt())
    }

    override suspend fun updateUser(user: User): Int {
        return userDao.update(user)
    }

    override suspend fun deleteUser(id: Int): Boolean {
        val user = userDao.getById(id)
        return if (user != null) {
            userDao.delete(user)
            true
        } else {
            false
        }
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }
}