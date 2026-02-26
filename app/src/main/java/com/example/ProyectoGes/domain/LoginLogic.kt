package com.example.ProyectoGes.domain

import com.example.ProyectoGes.models.User
import com.example.ProyectoGes.data.RoomUserRepository

class LogicLogin(private val userRepository: RoomUserRepository) {

    /*fun comprobarLogin(email: String, password: String): User {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Los campos no pueden estar vacíos.")
        }

        val user = RepositorioDeInicioDeSesion.obtenerUsuarios()
            .find { it.email.equals(email.trim(), ignoreCase = true) && it.password == password }
            ?: throw IllegalArgumentException("Email o contraseña incorrectos.")

        return user
    }*/

    suspend fun comprobarLogin(email: String, password: String): User {
        if (email.isBlank()) {
            throw IllegalArgumentException("El email no puede estar vacío")
        }
        if (password.isBlank()) {
            throw IllegalArgumentException("La contraseña no puede estar vacía")
        }

        val user = userRepository.login(email, password)
            ?: throw IllegalArgumentException("Email o contraseña incorrectos")

        return user
    }
}
