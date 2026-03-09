// UserRoles.kt
package com.example.proyectoGes.models

object UserRoles {

    val allRoles = listOf(
        "ADMIN_DEPORTIVO" to "Administrador Deportivo",
        "ENTRENADOR"      to "Entrenador",
        "JUGADOR"         to "Jugador",
        "ARBITRO"         to "Árbitro"
    )


    val rolesConEquipo = setOf("JUGADOR", "ENTRENADOR")

    val posiciones = listOf(
        "Portero",
        "Delantero",
        "Centrocampista"
    )

    val equipos = listOf(
        "Equipo A",
        "Equipo B",
        "Equipo C",
        "Equipo Juvenil",
        "Sin equipo"
    )
}