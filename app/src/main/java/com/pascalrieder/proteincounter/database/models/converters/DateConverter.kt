package com.pascalrieder.proteincounter.database.models.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.Date


object DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long): LocalDate {
        return LocalDate.ofEpochDay(dateLong)
    }

    @TypeConverter
    fun fromDate(date: LocalDate): Long {
        return date.toEpochDay()
    }
}