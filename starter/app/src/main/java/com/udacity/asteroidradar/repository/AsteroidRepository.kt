package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.*
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.getFormattedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

class AsteroidRepository(private val database: AsteroidsDatabase) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    val pictureOfDay: LiveData<List<PictureOfDay>> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDay()) {
            it.asDomain()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            val currentTime = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
            val weekTime = calendar.time
            val asteroids = Network.asteroids.getAsteroidsAsync(
                currentTime.getFormattedDate(),
                weekTime.getFormattedDate()
            ).await()
            database.asteroidDao.deleteNotSaved()
            database.asteroidDao.insertAll(
                *parseAsteroidsJsonResult(JSONObject(asteroids)).asDatabaseModel().toTypedArray()
            )
        }
    }

    suspend fun getImageOfTheDay() {
        withContext(Dispatchers.IO) {
            val picture = Network.asteroids.getImageOfTheDayAsync().await()
            database.pictureOfDayDao.insertAll(*listOf(picture).asDatabase().toTypedArray())
        }
    }

    suspend fun updateAsteroid(asteroid: Asteroid) {
        withContext(Dispatchers.IO) {
            database.asteroidDao.update(asteroid.asDatabase())
        }
    }
}