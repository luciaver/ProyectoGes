package com.example.proyectoGes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "instalaciones")
data class Facility(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val disponible: Boolean = true
)