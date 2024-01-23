package com.pascalrieder.proteincounter.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pascalrieder.proteincounter.database.models.Day
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DayWithItems(
    val dayId: Long = 0, val date: LocalDate, val items: MutableList<ItemFromDay> = mutableListOf()
) {
    fun toDay(): Day {
        return Day(uid = dayId, date)
    }
    fun getFormattedDate(): String {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    fun getKcalTotal(): Float {
        var kcal = 0f
        items.forEach {
            kcal += it.getKcalContent()
        }
        return kcal
    }

    fun getProteinTotal(): Float {
        var kcal = 0f
        items.forEach {
            kcal += it.getProteinContentInGram()
        }
        return kcal
    }
}

class ItemFromDay(
    val itemId: Long,
    val name: String,
    val proteinContentPercentage: Float,
    val kcalContentIn100g: Float,
    val amountInGram: Float,
) {
    fun getProteinContentInGram(): Float {
        return (proteinContentPercentage / 100) * amountInGram
    }

    fun getKcalContent(): Float {
        return (kcalContentIn100g / 100) * amountInGram
    }
}


@Entity
class DayWithItemsDb(
    @PrimaryKey val dayId: Long,
    val date: LocalDate,
    val itemId: Long?,
    val name: String?,
    val proteinContentPercentage: Float?,
    val kcalContentIn100g: Float?,
    val amountInGram: Float?,
    val isDeleted: Boolean?
)
