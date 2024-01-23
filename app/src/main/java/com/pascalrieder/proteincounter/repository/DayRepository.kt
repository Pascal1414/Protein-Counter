package com.m335pascal.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.pascalrieder.proteincounter.database.dao.DayDao
import com.pascalrieder.proteincounter.database.dto.DayWithItems
import com.pascalrieder.proteincounter.database.dto.DayWithItemsDb
import com.pascalrieder.proteincounter.database.dto.ItemFromDay
import com.pascalrieder.proteincounter.database.models.DayItem
import java.time.LocalDate

class DayRepository(private val dayDao: DayDao) {
    private val allDaysWithItems: MediatorLiveData<List<DayWithItems>> = MediatorLiveData()
    private val todayWithItems: MediatorLiveData<DayWithItems> = MediatorLiveData()
    var onTodayNotFound: () -> Unit = {}

    init {
        allDaysWithItems.addSource(dayDao.readAllData()) { dbDays ->
            allDaysWithItems.value = dayEntriesToDayWithItems(dbDays)
        }
        todayWithItems.addSource(dayDao.readDayEntriesFromDate(LocalDate.now())) { dbDays ->
            val days = dayEntriesToDayWithItems(dbDays)
            if (days.isNotEmpty() && days.count() == 1) todayWithItems.value = days.first()
            else onTodayNotFound()
        }

    }

    fun getDaysWithItems(): LiveData<List<DayWithItems>> {
        return allDaysWithItems
    }

    fun getToday(onDayNotFound: () -> Unit = {}): LiveData<DayWithItems> {
        onTodayNotFound = onDayNotFound
        return todayWithItems;
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
                if (dbDay.itemId != null && !dbDay.isDeleted!!) newDay.items.add(
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
                if (dbDay.itemId != null && !dbDay.isDeleted!!) existingDay.items.add(
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