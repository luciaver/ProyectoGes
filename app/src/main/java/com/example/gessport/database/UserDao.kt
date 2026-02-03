package com.example.gessport.database

import androidx.room.*
import com.example.gessport.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    // READ
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM usuarios WHERE rol = :role ORDER BY nombre ASC")
    fun getByRole(role: String): Flow<List<User>>

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): User?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun getUserCount(): Int

    // UPDATE
    @Update
    suspend fun update(user: User): Int

    // DELETE
    @Delete
    suspend fun delete(user: User)

    @Query("DELETE FROM usuarios")
    suspend fun deleteAll()
}