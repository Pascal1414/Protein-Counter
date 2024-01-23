package com.pascalrieder.proteincounter.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pascalrieder.proteincounter.database.models.Day
import java.time.LocalDate

class DayWithItems(
    val dayId: Long = 0,
    val date: String,
    val items: MutableList<ItemFromDay>
) {
    fun toDay(): Day {
        return Day(uid = dayId, date)
    }
}

class ItemFromDay(
    val itemId: Long,
    val name: String,
    val proteinContentPercentage: Float,
    val kcalContentIn100g: Float,
    val amountInGram: Float,
)


@Entity
class DayWithItemsDb(
    @PrimaryKey val dayId: Long,
    val date: String,
    val itemId: Long?,
    val name: String?,
    val proteinContentPercentage: Float?,
    val kcalContentIn100g: Float?,
    val amountInGram: Float?
)
