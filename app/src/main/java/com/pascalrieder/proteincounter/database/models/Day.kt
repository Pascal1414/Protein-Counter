package com.pascalrieder.proteincounter.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pascalrieder.proteincounter.database.models.Item
import java.time.LocalDate

@Entity(tableName = "day")

data class Day (
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "date") val date: LocalDate,
)