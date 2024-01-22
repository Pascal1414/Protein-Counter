package com.pascalrieder.proteincounter.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true) val uid: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "proteinContentPercentage") val proteinContentPercentage: Float,
    @ColumnInfo(name = "kcalContentIn100g") val kcalContentIn100g: Float,
    @ColumnInfo(name = "isDeleted") var isDeleted: Boolean = false
)