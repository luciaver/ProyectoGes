package com.example.proyectoGes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class Team(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val deporte: String,
    val entrenadorId: Int = 0
)