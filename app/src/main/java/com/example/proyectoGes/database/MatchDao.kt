package com.example.proyectoGes.database

import androidx.room.*
import com.example.proyectoGes.models.Match
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(match: Match): Long

    @Query("SELECT * FROM partidos ORDER BY fecha ASC, hora ASC")
    fun getAll(): Flow<List<Match>>

    @Query("SELECT * FROM partidos WHERE arbitroId = :arbitroId")
    fun getByArbitro(arbitroId: Int): Flow<List<Match>>

    @Query("SELECT * FROM partidos WHERE equipo1 = :equipo OR equipo2 = :equipo")
    fun getByEquipo(equipo: String): Flow<List<Match>>

    @Query("SELECT * FROM partidos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Match?

    @Query("SELECT COUNT(*) FROM partidos")
    suspend fun getCount(): Int

    @Update
    suspend fun update(match: Match): Int

    @Delete
    suspend fun delete(match: Match)
}