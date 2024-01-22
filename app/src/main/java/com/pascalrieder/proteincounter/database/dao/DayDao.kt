package com.m335pascal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.m335pascal.database.dto.DayWithItemsDb
import com.pascalrieder.proteincounter.database.models.Day
import java.time.LocalDate

@Dao
interface DayDao {
    @Query(
        "SELECT Day.uid AS dayId, Day.date, Item.uid AS itemId, Item.name, Item.proteinContentPercentage, Item.kcalContentIn100g, DayItem.amountInGram FROM Day LEFT JOIN DayItem ON Day.uid = DayItem.dayId LEFT JOIN Item ON DayItem.itemId = Item.uid;"
    )
    fun readAllData(): LiveData<List<DayWithItemsDb>>

    @Query(
        "SELECT Day.uid AS dayId, Day.date, Item.uid AS itemId, Item.name, Item.proteinContentPercentage, Item.kcalContentIn100g, DayItem.amountInGram FROM Day LEFT JOIN DayItem ON Day.uid = DayItem.dayId LEFT JOIN Item ON DayItem.itemId = Item.uid WHERE Day.date = :date;"
    )
    fun readDayEntriesFromDate(date: LocalDate): LiveData<List<DayWithItemsDb>>

    @Insert
    fun addDay(day: Day): Long
    @Query("UPDATE DAYITEM SET isDeleted = 1 WHERE dayId = :dayId AND itemId = :itemId")
    fun removeItemFromDay(dayId: Long, itemId: Long)

    @Query("Insert Into DayItem (dayId, itemId, amountInGram) values (:dayId,:itemId,:amount)")
    fun addItemToDay(dayId: Long, itemId: Long, amount: Float)
}