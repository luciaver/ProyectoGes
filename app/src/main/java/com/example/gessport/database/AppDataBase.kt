package com.example.gessport.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gessport.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

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
                        populateDatabase(database.userDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(userDao: UserDao) {
            // Solo insertar si la base está vacía
            if (userDao.getUserCount() == 0) {
                val defaultUsers = listOf(
                    User(0, "Lolo Pérez", "lolo.admin@club.es", "1234", "ADMIN_DEPORTIVO"),
                    User(0, "Pedro Caselles", "pedro.entrenador@club.es", "1234", "ENTRENADOR"),
                    User(0, "Pepa Ferrández", "laura.jugadora@club.es", "1234", "JUGADOR"),
                    User(0, "Pablo Teruel", "luis.arbitro@club.es", "1234", "ARBITRO"),
                    User(0, "María Belmonte", "maria.jugadora@club.es", "1234", "JUGADOR")
                )

                userDao.insertAll(defaultUsers)
            }
        }
    }
}