/*
 * Copyright (c) 2023.
 *
 *  Developed by : Bigad Aboubakr
 *  Developer website : http://bigad.me
 *  Developer github : https://github.com/Scout4all
 *  Developer Email : bigad@bigad.me
 */

package me.bigad.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidsDao {


    @Query("select * from asteroid order by closeApproachDate asc")
    fun getAllAsteroids(): LiveData<List<EntityAsteroid>>

    @Query("select id from asteroid limit 1 ")
    suspend fun testNotEmpty(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg asteroids: EntityAsteroid)

    @Query("select * from photo_of_day limit 1")
    fun getPhotoOfDay(): LiveData<EntityPhotoOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotoOfDay(photoOfDay: EntityPhotoOfDay)

    @Query("delete from asteroid  where closeApproachDate =:date")
    suspend fun deleteOldData(date : String)

    @Query("delete from photo_of_day")
    suspend fun deletePhotos()
}