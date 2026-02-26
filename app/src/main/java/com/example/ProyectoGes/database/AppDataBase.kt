package com.example.ProyectoGes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ProyectoGes.models.Facility
import com.example.ProyectoGes.models.Reservation
import com.example.ProyectoGes.models.Team
import com.example.ProyectoGes.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Team::class, Reservation::class, Facility::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // ── DAOs ──────────────────────────────────────────────────
    abstract fun userDao(): UserDao
    abstract fun teamDao(): TeamDao
    abstract fun reservationDao(): ReservationDao
    abstract fun facilityDao(): FacilityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gessport_db"
                )
                    .fallbackToDestructiveMigration() // borra y recrea si cambia version
                    .addCallback(DatabaseCallback(context))
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(db: AppDatabase) {
            // Usuarios por defecto
            if (db.userDao().getUserCount() == 0) {
                db.userDao().insertAll(
                    listOf(
                        User(0, "Lolo Pérez", "lolo.admin@club.es", "1234", "ADMIN_DEPORTIVO"),
                        User(0, "Pedro Caselles", "pedro.entrenador@club.es", "1234", "ENTRENADOR"),
                        User(0, "Pepa Ferrández", "pepa.jugadora@club.es", "1234", "JUGADOR"),
                        User(0, "Pablo Teruel", "pablo.arbitro@club.es", "1234", "ARBITRO"),
                        User(0, "María Belmonte", "maria.jugadora@club.es", "1234", "JUGADOR")
                    )
                )
            }

            // Instalaciones por defecto
            if (db.facilityDao().getCount() == 0) {
                db.facilityDao().insertAll(
                    listOf(
                        Facility(0, "Pista Pádel 1", "Pádel", true, 4),
                        Facility(0, "Pista Pádel 2", "Pádel", true, 4),
                        Facility(0, "Pista Tenis A", "Tenis", true, 2),
                        Facility(0, "Campo Fútbol", "Fútbol", true, 22),
                        Facility(0, "Gimnasio", "Multideporte", true, 30)
                    )
                )
            }

            // Equipos por defecto
            if (db.teamDao().getCount() == 0) {
                db.teamDao().insertAll(
                    listOf(
                        Team(0, "Pádel Azul", "Pádel", 2, 4),
                        Team(0, "Tenis Senior", "Tenis", 2, 2),
                        Team(0, "Fútbol 7", "Fútbol", 2, 7)
                    )
                )
            }
        }
    }
}