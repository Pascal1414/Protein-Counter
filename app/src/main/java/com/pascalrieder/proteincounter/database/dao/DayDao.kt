package com.pascalrieder.proteincounter.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pascalrieder.proteincounter.database.dto.DayWithItemsDb
import com.pascalrieder.proteincounter.database.models.Day
import java.time.LocalDate

@Dao
interface DayDao {
    @Query(
        "SELECT Day.uid AS dayId, Day.date, Item.uid AS itemId, Item.name, Item.proteinContentPercentage, Item.kcalContentIn100g, DayItem.amountInGram, DayItem.isDeleted FROM Day LEFT JOIN DayItem ON Day.uid = DayItem.dayId LEFT JOIN Item ON DayItem.itemId = Item.uid WHERE DayItem.isDeleted = 0;"
    )
    fun readAllData(): LiveData<List<DayWithItemsDb>>

    @Query(
        "SELECT Day.uid AS dayId, Day.date, Item.uid AS itemId, Item.name, Item.proteinContentPercentage, Item.kcalContentIn100g, DayItem.amountInGram, DayItem.isDeleted FROM Day LEFT JOIN DayItem ON Day.uid = DayItem.dayId LEFT JOIN Item ON DayItem.itemId = Item.uid WHERE Day.date = :date;"
    )
    fun readDayEntriesFromDate(date: LocalDate): LiveData<List<DayWithItemsDb>>

    @Insert
    fun addDay(day: Day): Long
    @Query("UPDATE DAYITEM SET isDeleted = 1 WHERE dayId = :dayId AND itemId = :itemId")
    fun removeItemFromDay(dayId: Long, itemId: Long)

    @Query("Insert Into DayItem (dayId, itemId, amountInGram, isDeleted) values (:dayId, :itemId, :amount, :isDeleted);")
    fun addItemToDay(dayId: Long, itemId: Long, amount: Float, isDeleted: Boolean)
}