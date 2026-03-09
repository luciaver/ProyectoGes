package com.example.proyectoGes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class Team(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val deporte: String,       // "Padel", "Tenis", "Futbol", etc.
    val entrenadorId: Int = 0, // ID del entrenador asignado
    val numJugadores: Int = 0
)