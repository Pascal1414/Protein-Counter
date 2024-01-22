package com.pascalrieder.proteincounter.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate


@Entity(tableName = "day", indices = [Index(value = ["date"], unique = true)])
data class Day(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "date") val date: LocalDate,
)