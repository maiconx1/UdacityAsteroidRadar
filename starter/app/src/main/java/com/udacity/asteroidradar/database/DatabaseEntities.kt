package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid

@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val codename: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val closeApproachDate: String,
    val isPotentiallyHazardous: Boolean,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
)

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return this.map {
        Asteroid(
            it.id,
            it.codename,
            it.closeApproachDate,
            it.absoluteMagnitude,
            it.estimatedDiameter,
            it.relativeVelocity,
            it.distanceFromEarth,
            it.isPotentiallyHazardous
        )
    }
}