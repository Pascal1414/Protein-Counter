package com.pascalrieder.proteincounter.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DayItem(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    val dayId: Int,
    val itemId: Int,
    @ColumnInfo(name = "amountInGram") val amountInGram: Float,
    @ColumnInfo(name = "isDeleted") var isDeleted: Boolean = false
)
