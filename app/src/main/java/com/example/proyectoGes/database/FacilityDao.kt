package com.example.proyectoGes.database

import androidx.room.*
import com.example.proyectoGes.models.Facility
import kotlinx.coroutines.flow.Flow

@Dao
interface FacilityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(facility: Facility): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(facilities: List<Facility>)

    @Query("SELECT * FROM instalaciones ORDER BY nombre ASC")
    fun getAll(): Flow<List<Facility>>

    @Query("SELECT * FROM instalaciones WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Facility?

    @Query("SELECT COUNT(*) FROM instalaciones")
    suspend fun getCount(): Int

    @Update
    suspend fun update(facility: Facility): Int

    @Delete
    suspend fun delete(facility: Facility)
}