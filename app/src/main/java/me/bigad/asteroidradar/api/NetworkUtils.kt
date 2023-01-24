package me.bigad.asteroidradar.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.bigad.asteroidradar.database.EntityAsteroid
import me.bigad.asteroidradar.database.EntityPhotoOfDay
import me.bigad.asteroidradar.domain.Asteroid
import me.bigad.asteroidradar.domain.Constants
import me.bigad.asteroidradar.domain.PhotoOfDay
import org.json.JSONObject
import timber.log.Timber
import java.util.*


fun parseAsteroidsJsonResult(jsonResult: JSONObject): ArrayList<NetworkAsteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

    val asteroidList = ArrayList<NetworkAsteroid>()

    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    for (formattedDate in nextSevenDaysFormattedDates) {
        if (nearEarthObjectsJson.has(formattedDate)) {
            val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)

            for (i in 0 until dateAsteroidJsonArray.length()) {
                val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
                val id = asteroidJson.getLong("id")
                val codename = asteroidJson.getString("name")
                val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
                val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                    .getJSONObject("kilometers").getDouble("estimated_diameter_max")

                val closeApproachData = asteroidJson
                    .getJSONArray("close_approach_data").getJSONObject(0)
                val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                    .getDouble("kilometers_per_second")
                val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                    .getDouble("astronomical")
                val isPotentiallyHazardous = asteroidJson
                    .getBoolean("is_potentially_hazardous_asteroid")

                val asteroid = NetworkAsteroid(
                    id, codename, formattedDate, absoluteMagnitude,
                    estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous
                )
                asteroidList.add(asteroid)
            }
        }
    }

    return asteroidList
}


private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time


        formattedDateList.add(Constants.dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}

data class NetworkAsteroid(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

data class NetworkAsteroidContainer(val asteroids: List<NetworkAsteroid> = listOf())

//convert server response to Network Asteroid Object
fun String.asNetworkModel(): NetworkAsteroidContainer {
    if (this.isNotEmpty()) {
        val obj = JSONObject(this)
        val parsedAstroids = parseAsteroidsJsonResult(obj)
        return NetworkAsteroidContainer(parsedAstroids)
    }
    return NetworkAsteroidContainer()

}

//convert server response to Domain object in case of load data from network
fun NetworkAsteroidContainer.asDomainModel(): List<Asteroid> {
    return asteroids.map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}
//convert server response to Database object
fun NetworkAsteroidContainer.asDatabaseModel(): Array<EntityAsteroid> {
    Timber.w(this.asteroids.get(0).toString())
    return asteroids.map {
        EntityAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )

    }.toTypedArray()
}

@JsonClass(generateAdapter = true)
data class NetworkPhotoOfDay(
    @Json(name = "media_type") val mediaType: String, val title: String,
    val url: String
)

fun NetworkPhotoOfDay.asDatabaseModel(): EntityPhotoOfDay {
    return EntityPhotoOfDay(
        url = url,
        mediaType = mediaType,
        title = title
    )
}

fun NetworkPhotoOfDay.asDomainModel(): PhotoOfDay {
    return PhotoOfDay(mediaType, title, url)
}