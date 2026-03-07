package com.example.ProyectoGes.database

import androidx.room.*
import com.example.ProyectoGes.models.Reservation
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reservation: Reservation): Long

    @Query("SELECT * FROM reservas ORDER BY fecha ASC, horaInicio ASC")
    fun getAll(): Flow<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE userId = :userId ORDER BY fecha ASC")
    fun getByUser(userId: Int): Flow<List<Reservation>>

    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Reservation?

    @Query("SELECT COUNT(*) FROM reservas")
    suspend fun getCount(): Int

    // Devuelve las horas ya reservadas para una instalación y fecha concretas
    @Query("SELECT horaInicio FROM reservas WHERE instalacionId = :facilityId AND fecha = :fecha")
    suspend fun getBookedSlots(facilityId: Int, fecha: String): List<String>

    @Update
    suspend fun update(reservation: Reservation): Int

    @Delete
    suspend fun delete(reservation: Reservation)
}