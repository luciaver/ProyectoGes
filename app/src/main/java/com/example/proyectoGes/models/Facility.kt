package com.example.proyectoGes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "instalaciones")
data class Facility(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String,      // "Pista de Padel", "Pista de Tenis", "Campo de Futbol", etc.
    val disponible: Boolean = true,
    val capacidad: Int = 4
)