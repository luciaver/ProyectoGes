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
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

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
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // ── Callback ────────────────────────────────────────────────────────
        // onOpen cubre el caso de fallbackToDestructiveMigration: Room destruye
        // y recrea la BD pero NO relanza onCreate, así que comprobamos en cada
        // apertura si las tablas están vacías y las rellenamos si hace falta.
        private class DatabaseCallback : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seed()
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                seed()
            }

            private fun seed() {
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateIfEmpty(database)
                    }
                }
            }
        }

        private suspend fun populateIfEmpty(db: AppDatabase) {
            if (db.userDao().getUserCount() == 0) {
                db.userDao().insertAll(
                    listOf(
                        User(0, "Lolo Pérez",     "lolo.admin@club.es",       "1234", "ADMIN_DEPORTIVO"),
                        User(0, "Pedro Caselles", "pedro.entrenador@club.es", "1234", "ENTRENADOR"),
                        User(0, "Pepa Ferrández", "pepa.jugadora@club.es",    "1234", "JUGADOR"),
                        User(0, "Pablo Teruel",   "pablo.arbitro@club.es",    "1234", "ARBITRO"),
                        User(0, "María Belmonte", "maria.jugadora@club.es",   "1234", "JUGADOR")
                    )
                )
            }

            if (db.facilityDao().getCount() == 0) {
                db.facilityDao().insertAll(
                    listOf(
                        Facility(0, "Pista Pádel",     "Pádel",      true, 4),
                        Facility(0, "Pista Tenis",     "Tenis",      true, 2),
                        Facility(0, "Campo Fútbol",    "Fútbol",     true, 22),
                        Facility(0, "Pista Baloncesto","Baloncesto", true, 10)
                    )
                )
            }

            if (db.teamDao().getCount() == 0) {
                db.teamDao().insertAll(
                    listOf(
                        Team(0, "Equipo Pádel",      "Pádel",      0, 4),
                        Team(0, "Equipo Tenis",      "Tenis",      0, 2),
                        Team(0, "Equipo Fútbol",     "Fútbol",     0, 11),
                        Team(0, "Equipo Baloncesto", "Baloncesto", 0, 5)
                    )
                )
            }
        }
    }
}