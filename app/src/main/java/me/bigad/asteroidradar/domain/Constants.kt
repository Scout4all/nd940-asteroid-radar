package me.bigad.asteroidradar.domain

import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val API_QUERY_DATE_FORMAT = "yyyy-mm-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api.nasa.gov/"
    const val API_KEY = "TyMLT99MgexF1nuAyzkxklYBmKn67CKgTod1csRY"

    private val calendar = Calendar.getInstance()
    val currentTime = calendar.time
    val dateFormat = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.US);

    val TODAY_DATE: String = dateFormat.format(currentTime)

}