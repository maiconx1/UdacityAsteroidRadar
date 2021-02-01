package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("delete from databaseasteroid where not saved")
    fun deleteNotSaved()

    @Update
    fun update(asteroid: DatabaseAsteroid)
}

@Dao
interface PictureOfDayDao {
    @Query("select * from databasepictureofday")
    fun getPictureOfDay(): LiveData<List<DatabasePictureOfDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg pictures: DatabasePictureOfDay)
}

@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val pictureOfDayDao: PictureOfDayDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroid"
            ).build()
        }
    }
    return INSTANCE
}