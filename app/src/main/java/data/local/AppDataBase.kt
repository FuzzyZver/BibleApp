package data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    // ДОБАВЬТЕ ЭТОТ КОМПАНЬОН-ОБЪЕКТ (Singleton)
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Если INSTANCE уже существует, просто вернем его
            return INSTANCE ?: synchronized(this) {
                // Если INSTANCE все еще null, создаем базу данных
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bible_app_db" // Имя вашего файла базы данных
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}