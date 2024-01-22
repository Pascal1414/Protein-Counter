package com.pascalrieder.proteincounter.repository

import androidx.lifecycle.LiveData
import com.pascalrieder.proteincounter.database.dao.ItemDao
import com.pascalrieder.proteincounter.database.models.Item

class ItemRepository(private val itemDao: ItemDao) {
    val readAllData: LiveData<List<Item>> = itemDao.readAllData()

    suspend fun addItem(item: Item) {
        itemDao.addItem(item)
    }
    suspend fun deleteItem(item: Item) {
        item.isDeleted = true
        itemDao.updateItem(item)
    }
}