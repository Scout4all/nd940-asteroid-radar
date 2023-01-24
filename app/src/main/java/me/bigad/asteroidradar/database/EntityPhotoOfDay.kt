package me.bigad.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.bigad.asteroidradar.domain.PhotoOfDay

@Entity(tableName = "photo_of_day")
class EntityPhotoOfDay(
    @PrimaryKey
    val url: String,
    val mediaType: String,
    val title: String
)

fun EntityPhotoOfDay.asDomainModel(): PhotoOfDay {
    return PhotoOfDay(
        mediaType = mediaType,
        title = title,
        url = url
    )

}