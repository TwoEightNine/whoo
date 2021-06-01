package global.msnthrp.whoo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import global.msnthrp.whoo.data.trip.room.model.TripDao
import global.msnthrp.whoo.data.trip.room.model.entity.TripEntity


@Database(entities = [TripEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

    companion object {

        private var instance: AppDatabase? = null

        fun getInstance(applicationContext: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "whoo-db"
                ).build()
                    .also { instance = it }
            }
        }
    }
}