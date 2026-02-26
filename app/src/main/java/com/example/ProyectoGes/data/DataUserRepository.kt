package com.example.ProyectoGes.data

//class DataUserRepository : UserRepository {
//
//    private fun getNewId(): Int {
//        return (UserDataSource.users.maxOfOrNull { it.id } ?: 0) + 1
//    }
//
//    override suspend fun addUser(user: User): User {
//        val newId = getNewId()
//        val newUser = user.copy(id = newId)
//        UserDataSource.users.add(newUser)
//        return newUser
//    }
//
//    override suspend fun getUserById(id: Int): User? {
//        return UserDataSource.users.find { it.id == id }
//    }
//
//    override suspend fun updateUser(user: User): Boolean {
//        val index = UserDataSource.users.indexOfFirst { it.id == user.id }
//        return if (index != -1) {
//            UserDataSource.users[index] = user
//            true
//        } else {
//            false
//        }
//    }
//
//    override suspend fun deleteUser(id: Int): Boolean {
//        return UserDataSource.users.removeIf { it.id == id }
//    }
//
//    override suspend fun getAllUsers(): List<User> {
//        return UserDataSource.users.toList()
//    }
//
//    override suspend fun getUsersByRole(rol: String): List<User> =
//        UserDataSource.users.filter { it.rol == rol }
//}
