package com.example.ProyectoGes.models

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
    val fecha: String,      // "dd/MM/yyyy"
    val horaInicio: String, // "HH:mm"
    val horaFin: String,    // "HH:mm"
    val tipo: String = "individual" // "individual" o "equipo"
)