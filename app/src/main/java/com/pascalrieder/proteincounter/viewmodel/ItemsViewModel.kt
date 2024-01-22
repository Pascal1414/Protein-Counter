package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.pascalrieder.proteincounter.database.AppDatabase
import com.pascalrieder.proteincounter.database.models.Item
import com.pascalrieder.proteincounter.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemsViewModel(application: Application) : AndroidViewModel(application) {
    var onFabClick: () -> Unit = {}


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
}