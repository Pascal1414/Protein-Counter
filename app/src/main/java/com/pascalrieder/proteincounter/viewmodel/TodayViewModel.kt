package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

    private val sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences(
        "com.pascalrieder.proteincounter", Context.MODE_PRIVATE
    )
    private val kcalGoalName = "kcalGoal"
    private val proteinGoalName = "proteinGoal"


    var kcalGoal by mutableIntStateOf(2000)
    var proteinGoal by mutableIntStateOf(100)

    fun setKcalGoalValue(value: Int) {
        kcalGoal = value

        with(sharedPref.edit()) {
            putInt(kcalGoalName, value)
            apply()
        }
    }


    var dialogKcalGoal by mutableStateOf("")
    fun updateDialogKcalGoal(value: String) {
        if (value == "") {
            dialogKcalGoal = ""
            return
        }
        val number = value.filter { it.isDigit() }
        val intOrNull = number.toIntOrNull() ?: return
        if (intOrNull > 99999) return
        dialogKcalGoal = intOrNull.toString()
    }

    fun onSetKcalGoalClick() {
        if (dialogKcalGoal == "") return
        val intOrNull = dialogKcalGoal.toIntOrNull() ?: return
        setKcalGoalValue(intOrNull)
        dialogKcalGoal = ""
    }

    fun setProteinGoalValue(value: Int) {
        proteinGoal = value

        with(sharedPref.edit()) {
            putInt(proteinGoalName, value)
            apply()
        }
    }

    var dialogProteinGoal by mutableStateOf("")
    fun updateDialogProteinGoal(value: String) {
        if (value == "") {
            dialogProteinGoal = ""
            return
        }
        val number = value.filter { it.isDigit() }
        val intOrNull = number.toIntOrNull() ?: return
        if (intOrNull > 99999) return
        dialogProteinGoal = intOrNull.toString()
    }

    fun onSetProteinGoalClick() {
        if (dialogProteinGoal == "") return
        val intOrNull = dialogProteinGoal.toIntOrNull() ?: return
        setProteinGoalValue(intOrNull)
        dialogProteinGoal = ""
    }

    init {
        kcalGoal = sharedPref.getInt(kcalGoalName, 2000)
        proteinGoal = sharedPref.getInt(proteinGoalName, 100)
    }


    fun insertItemClick(itemId: Long) {
        if (amountInGram.isEmpty()) errorMessage = "Please enter an amount"
        else {
            insertItem(itemId, amountInGram.toFloat())
            amountInGram = ""
            searchText = ""
            errorMessage = ""
            openBottomSheet = false
        }
    }

    fun insertItem(itemId: Long, amountInGram: Float) = viewModelScope.launch(Dispatchers.IO) {
        dayRepository.addItemToDay(
            DayItem(
                dayId = dayWithItems.value!!.dayId,
                itemId = itemId,
                amountInGram = amountInGram,
                isDeleted = false
            )
        )
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