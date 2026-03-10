package com.example.proyectoGes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val userName: String,
    val instalacionId: Int,
    val instalacionNombre: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val precio: Double = 0.0,
    val tipo: String = "individual"
)