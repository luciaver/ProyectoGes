package com.example.proyectoGes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidos")
data class Match(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val equipo1: String,
        val equipo2: String,
        val fecha: String,
        val hora: String,
        val instalacionNombre: String = "",
        val arbitroId: Int = 0,
        val arbitroNombre: String = "",
        val resultado: String = "Pendiente"
)