/*
 * Copyright (c) 2023.
 *
 *  Developed by : Bigad Aboubakr
 *  Developer website : http://bigad.me
 *  Developer github : https://github.com/Scout4all
 *  Developer Email : bigad@bigad.me
 */

package me.bigad.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.bigad.asteroidradar.database.getDatabase
import me.bigad.asteroidradar.domain.Constants
import me.bigad.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException
import java.util.*

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDeleteDataWorker"
    }

    /**
     * A coroutine-friendly method to do your work.
     * Note: In recent work version upgrade, 1.0.0-alpha12 and onwards have a breaking change.
     * The doWork() function now returns Result instead of Payload because they have combined Payload into Result.
     * Read more here - https://developer.android.com/jetpack/androidx/releases/work#1.0.0-alpha12
     */
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
      val yesterday =  Constants.calendar.add(Calendar.DAY_OF_YEAR, -1)
        val formattedYesterday = Constants.dateFormat.format(yesterday)
        return try {
            repository.refreshAsteroids()
            repository.refreshPhotoOfDay()
            repository.deleteDailyAsteroids(formattedYesterday)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}
