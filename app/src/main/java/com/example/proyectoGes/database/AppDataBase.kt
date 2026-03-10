package com.example.proyectoGes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectoGes.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Team::class, Reservation::class, Facility::class, Match::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun teamDao(): TeamDao
    abstract fun reservationDao(): ReservationDao
    abstract fun facilityDao(): FacilityDao
    abstract fun matchDao(): MatchDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gessport_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { populate(it) }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private suspend fun populate(db: AppDatabase) {
            if (db.userDao().getUserCount() == 0) {
                db.userDao().insertAll(listOf(
                    User(0, "Admin Deportivo",  "admin@club.es",  "1234", "ADMIN_DEPORTIVO"),
                    User(0, "Pedro Entrenador", "pedro@club.es",  "1234", "ENTRENADOR", equipo = "Equipo Fútbol"),
                    User(0, "Ana Jugadora",     "ana@club.es",    "1234", "JUGADOR", posicion = "Delantera", equipo = "Equipo Fútbol"),
                    User(0, "Carlos Árbitro",   "carlos@club.es", "1234", "ARBITRO"),
                    User(0, "Luis Jugador",     "luis@club.es",   "1234", "JUGADOR", posicion = "Portero", equipo = "Equipo Pádel")
                ))
            }
            if (db.facilityDao().getCount() == 0) {
                db.facilityDao().insertAll(listOf(
                    Facility(0, "Pista Pádel",      "Pádel",      true),
                    Facility(0, "Pista Tenis",      "Tenis",      true),
                    Facility(0, "Campo Fútbol",     "Fútbol",     true),
                    Facility(0, "Pista Baloncesto", "Baloncesto", true)
                ))
            }
            if (db.teamDao().getCount() == 0) {
                db.teamDao().insertAll(listOf(
                    Team(0, "Equipo Pádel",      "Pádel"),
                    Team(0, "Equipo Tenis",      "Tenis"),
                    Team(0, "Equipo Fútbol",     "Fútbol"),
                    Team(0, "Equipo Baloncesto", "Baloncesto")
                ))
            }
        }
    }
}