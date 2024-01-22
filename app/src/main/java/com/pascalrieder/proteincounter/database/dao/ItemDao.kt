package com.pascalrieder.proteincounter.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pascalrieder.proteincounter.database.models.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM item WHERE isDeleted = 0")
    fun readAllData(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addItem(item: Item)

    @Update
    fun updateItem(item: Item)

}