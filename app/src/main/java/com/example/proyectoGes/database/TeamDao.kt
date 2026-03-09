package com.example.proyectoGes.database

import androidx.room.*
import com.example.proyectoGes.models.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: Team): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(teams: List<Team>)

    @Query("SELECT * FROM equipos ORDER BY nombre ASC")
    fun getAll(): Flow<List<Team>>

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Team?

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun getCount(): Int

    @Update
    suspend fun update(team: Team): Int

    @Delete
    suspend fun delete(team: Team)
}