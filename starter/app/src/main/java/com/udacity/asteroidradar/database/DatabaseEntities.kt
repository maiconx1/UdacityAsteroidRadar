package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay

@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean,
    val saved: Boolean = false
)

@Entity
data class DatabasePictureOfDay constructor(
    @PrimaryKey
    val id: Long,
    val mediaType: String,
    val title: String,
    val url: String
)

fun DatabaseAsteroid.asDomain() = Asteroid(
    id,
    codename,
    closeApproachDate,
    absoluteMagnitude,
    estimatedDiameter,
    relativeVelocity,
    distanceFromEarth,
    isPotentiallyHazardous,
    saved
)

fun Asteroid.asDatabase() = DatabaseAsteroid(
    id,
    codename,
    closeApproachDate,
    absoluteMagnitude,
    estimatedDiameter,
    relativeVelocity,
    distanceFromEarth,
    isPotentiallyHazardous,
    saved
)

fun List<DatabasePictureOfDay>.asDomain(): List<PictureOfDay> {
    return this.map {
        PictureOfDay(it.mediaType, it.title, it.url)
    }
}

fun List<PictureOfDay>.asDatabase(): List<DatabasePictureOfDay> {
    return this.map {
        DatabasePictureOfDay(1, it.mediaType, it.title, it.url)
    }
}

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return this.map {
        it.asDomain()
    }
}

fun List<Asteroid>.asDatabaseModel(): List<DatabaseAsteroid> {
    return this.map {
        it.asDatabase()
    }
}