package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import android.os.Handler
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.pascalrieder.proteincounter.database.AppDatabase
import com.pascalrieder.proteincounter.database.dto.DayWithItems
import com.pascalrieder.proteincounter.database.dto.ItemFromDay
import com.m335pascal.repository.DayRepository
import com.pascalrieder.proteincounter.database.models.DayItem
import com.pascalrieder.proteincounter.database.models.Item
import com.pascalrieder.proteincounter.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class TodayViewModel(application: Application) : AndroidViewModel(application) {
    var openBottomSheet by mutableStateOf(false)

    var onFabClick: () -> Unit = {
        openBottomSheet = true
    }


    val dayWithItems: LiveData<DayWithItems>
    private val dayRepository: DayRepository

    val items: LiveData<List<Item>>
    private val itemRepository: ItemRepository


    var searchText by mutableStateOf("")
    var amountInGram by mutableStateOf("")
    var errorMessage by mutableStateOf("")

    init {
        val itemDao = AppDatabase.getDatabase(application).itemDao()
        itemRepository = ItemRepository(itemDao)
        items = itemRepository.readAllData

        val dayDao = AppDatabase.getDatabase(application).dayDao()
        dayRepository = DayRepository(dayDao)
        dayWithItems = dayRepository.getToday(onDayNotFound = {
            viewModelScope.launch(Dispatchers.IO) {
                dayRepository.addDay(DayWithItems(date = LocalDate.now(), items = mutableListOf()))
            }
        })
    }

    fun insertItem(itemId: Long) = viewModelScope.launch(Dispatchers.IO) {
        if (amountInGram.isEmpty()) errorMessage = "Please enter an amount"
        else {
            dayRepository.addItemToDay(
                DayItem(
                    dayId = dayWithItems.value!!.dayId,
                    itemId = itemId,
                    amountInGram = amountInGram.toFloat(),
                    isDeleted = false
                )
            )
            errorMessage = ""
            openBottomSheet = false
        }
    }

    fun removeItemFromToday(item: ItemFromDay) = viewModelScope.launch(Dispatchers.IO) {
        dayRepository.removeItemFromDay(dayWithItems.value!!.dayId, item.itemId)
    }

    fun isFloat(str: String): Boolean {
        return try {
            str.toFloat()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

}