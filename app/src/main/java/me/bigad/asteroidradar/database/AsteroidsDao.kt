package me.bigad.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface AsteroidsDao {


    @Query("select * from asteroid order by closeApproachDate")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("select id from asteroid limit 1 ")
    suspend fun testNotEmpty(): Long

    @Query("Select * from asteroid where closeApproachDate = :closeApproachDate")
    fun getAsteroidsByDay(closeApproachDate: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg asteroids: DatabaseAsteroid)

    @Query("select * from photo_of_day limit 1")
    fun getPhotoOfDay(): LiveData<DatabasePhotoOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoOfDay(photoOfDay: DatabasePhotoOfDay)
}