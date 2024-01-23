package com.pascalrieder.proteincounter.database.models.converters

import androidx.room.TypeConverter
import java.time.LocalDate


object DateConverter {
    @TypeConverter
    fun toDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString)
    }

    @TypeConverter
    fun fromDate(date: LocalDate): String {
        return date.toString()
    }
}