package com.example.proyectoGes.database

import androidx.room.*
import com.example.proyectoGes.models.Reservation
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

    /**
     * Detecta solapamientos: devuelve las reservas de una instalación en una fecha
     * cuyo intervalo [horaInicio, horaFin) se solapa con el intervalo pedido.
     * Dos intervalos [a,b) y [c,d) se solapan si a < d AND c < b.
     * Excluye la propia reserva si se está editando (excludeId = 0 para nuevas).
     */
    @Query("""
        SELECT * FROM reservas
        WHERE instalacionId = :facilityId
          AND fecha = :fecha
          AND id != :excludeId
          AND horaInicio < :horaFin
          AND horaFin   > :horaInicio
    """)
    suspend fun getOverlapping(
        facilityId: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        excludeId: Int = 0
    ): List<Reservation>

    /** Devuelve todas las reservas de una instalación en una fecha (para mostrar ocupadas) */
    @Query("SELECT * FROM reservas WHERE instalacionId = :facilityId AND fecha = :fecha")
    suspend fun getByFacilityAndDate(facilityId: Int, fecha: String): List<Reservation>

    @Update
    suspend fun update(reservation: Reservation): Int

    @Delete
    suspend fun delete(reservation: Reservation)
}