package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.pascalrieder.proteincounter.database.AppDatabase
import com.pascalrieder.proteincounter.database.models.Item
import com.pascalrieder.proteincounter.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemsViewModel(application: Application) : AndroidViewModel(application) {
    var openBottomSheet by mutableStateOf(false)
    var modalIsEditing by mutableStateOf(false)
    var modalEditItem by mutableStateOf<Item?>(null)

    var searchText by mutableStateOf("")

    var onFabClick: () -> Unit = {
        openBottomSheet = true
    }

    val allItems: LiveData<List<Item>>
    private val repository: ItemRepository

    init {
        val userDao = AppDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(userDao)
        allItems = repository.readAllData
    }

    fun removeItem(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItem(item)
    }

    var createItemName by mutableStateOf("")
    var createItemNameResponse by mutableStateOf("")
    var createItemProteinContentPercentage by mutableStateOf("")
    var createItemProteinContentPercentageResponse by mutableStateOf("")
    var createItemKcalContentIn100g by mutableStateOf("")
    var createItemKcalContentIn100gResponse by mutableStateOf("")


    fun createItemClick() = viewModelScope.launch(Dispatchers.IO) {
        createItemNameResponse = ""
        createItemProteinContentPercentageResponse = ""
        createItemKcalContentIn100gResponse = ""

        var success = true
        if (createItemName.isEmpty()) {
            createItemNameResponse = "Please enter a name"
            success = false
        }
        if (createItemProteinContentPercentage.isEmpty()) {
            createItemProteinContentPercentageResponse = "Please enter a protein content"
            success = false
        }
        if (createItemKcalContentIn100g.isEmpty()) {
            createItemKcalContentIn100gResponse = "Please enter a kcal content"
            success = false
        }
        if (success) {
            repository.addItem(
                Item(
                    name = createItemName,
                    proteinContentPercentage = createItemProteinContentPercentage.toFloat(),
                    kcalContentIn100g = createItemKcalContentIn100g.toFloat()
                )
            )
            createItemName = ""
            createItemProteinContentPercentage = ""
            createItemKcalContentIn100g = ""
            openBottomSheet = false
        }
    }

    fun startEditing(item: Item) {
        modalIsEditing = true
        modalEditItem = item
        openBottomSheet = true
    }

    fun stopEditing() {
        modalIsEditing = false
        modalEditItem = null
        openBottomSheet = false
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