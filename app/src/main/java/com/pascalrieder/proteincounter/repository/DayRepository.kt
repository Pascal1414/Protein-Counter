package com.m335pascal.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.m335pascal.database.dao.DayDao
import com.m335pascal.database.dto.DayWithItems
import com.m335pascal.database.dto.DayWithItemsDb
import com.m335pascal.database.dto.ItemFromDay
import com.pascalrieder.proteincounter.database.models.DayItem
import java.time.LocalDate

class DayRepository(private val dayDao: DayDao) {
    private val allDaysWithItems: MediatorLiveData<List<DayWithItems>> = MediatorLiveData()
    private val dayWithItems: MediatorLiveData<DayWithItems> = MediatorLiveData()

    init {
        allDaysWithItems.addSource(dayDao.readAllData()) { dbDays ->
            allDaysWithItems.value = dayEntriesToDayWithItems(dbDays)
        }
    }

    fun getDaysWithItems(): LiveData<List<DayWithItems>> {
        return allDaysWithItems
    }

    fun getDayFromDate(date: LocalDate, onDayNotFound: () -> Unit = {}): LiveData<DayWithItems> {
        dayWithItems.addSource(dayDao.readDayEntriesFromDate(date)) { dbDays ->
            val days = dayEntriesToDayWithItems(dbDays)
            if (days.isNotEmpty() && days.count() == 1)
                dayWithItems.value = days.first()
            else
                onDayNotFound()
        }
        return dayWithItems;
    }

    suspend fun removeItemFromDay(dayId: Long, itemId: Long) {
        dayDao.removeItemFromDay(dayId, itemId)
    }

    suspend fun addItemToDay(dayItem: DayItem) {
        dayDao.addItemToDay(
            itemId = dayItem.itemId,
            dayId = dayItem.dayId,
            amount = dayItem.amountInGram,
            isDeleted = dayItem.isDeleted
        )
    }

    suspend fun addDay(day: DayWithItems) {
        val a = dayDao.addDay(day.toDay())
        day.items.forEach {
            dayDao.addItemToDay(a, it.itemId, it.amountInGram, false)
        }
    }

    fun dayEntriesToDayWithItems(dayWithItemsDb: List<DayWithItemsDb>): MutableList<DayWithItems> {
        val days = mutableListOf<DayWithItems>()

        dayWithItemsDb.forEach { dbDay ->
            val existingDay = days.find { it.date == dbDay.date }

            if (existingDay == null) {
                val newDay = DayWithItems(dbDay.dayId, dbDay.date, mutableListOf())
                if (dbDay.itemId != null) newDay.items.add(
                    ItemFromDay(
                        itemId = dbDay.itemId,
                        name = dbDay.name.orEmpty(),
                        proteinContentPercentage = dbDay.proteinContentPercentage ?: 0.0f,
                        kcalContentIn100g = dbDay.kcalContentIn100g ?: 0.0f,
                        amountInGram = dbDay.amountInGram ?: 0.0f
                    )
                )
                days.add(newDay)
            } else {
                if (dbDay.itemId != null)
                    existingDay.items.add(
                        ItemFromDay(
                            itemId = dbDay.itemId ?: 0,
                            name = dbDay.name.orEmpty(),
                            proteinContentPercentage = dbDay.proteinContentPercentage ?: 0.0f,
                            kcalContentIn100g = dbDay.kcalContentIn100g ?: 0.0f,
                            amountInGram = dbDay.amountInGram ?: 0.0f
                        )
                    )
            }
        }
        return days
    }
}