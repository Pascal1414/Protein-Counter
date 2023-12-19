package com.pascalrieder.proteincounter.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pascalrieder.proteincounter.R
import java.time.LocalDate


class DataProvider {
    companion object {
        private var days = mutableListOf<Day>()
        private var additionalItems = mutableListOf<Item>()
         fun addItemToTodayAndCreateBackupIfNeeded(item: Item, context: Context) {
            var day = days.find { it.date == LocalDate.now() }
            if (day == null) {
                days.add(Day(LocalDate.now(), mutableListOf(item)))
                // Create Backup
                saveData(context)
            } else {
                day.items.add(item)
            }
        }

        fun getItems(date: LocalDate): List<Item> {
            return days.find { it.date == date }?.items?.toMutableList() ?: mutableListOf()
        }

         fun getItems(): List<Item> {
            // Does not return mutlible items with the same name and proteinContentPercentage
            val items = mutableListOf<Item>()
            days.forEach { day ->
                day.items.forEach { item ->
                    if (!items.any { it.name == item.name && it.proteinContentPercentage == item.proteinContentPercentage } && !item.isDeleted) {
                        items.add(item)
                    }
                }
            }
            items.addAll(additionalItems)
            return items
        }

        fun removeItem(item: Item) {
            days.forEach { day ->
                day.items.forEach { dayItem ->
                    if (dayItem.name == item.name && dayItem.proteinContentPercentage == item.proteinContentPercentage) {
                        dayItem.isDeleted = true
                    }
                }
            }
        }

         fun getDays(): List<Day> {
            return days.sortedByDescending { it.date }
        }

        fun getTodayConsumedProtein(): Float {
            var protein = 0f
            days.find { it.date == LocalDate.now() }?.items?.forEach { item ->
                protein += item.amountInGram * item.proteinContentPercentage / 100
            }
            return protein
        }

        fun getTodayConsumedKcal(): Float {
            var kcal = 0f
            days.find { it.date == LocalDate.now() }?.items?.forEach { item ->
                kcal += item.amountInGram * item.kcalContentIn100g / 100
            }
            return kcal
        }

        fun removeItemFromToday(item: Item) {
            var day = days.find { it.date == LocalDate.now() }
            day?.items?.remove(item)
        }

        private val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .create()

        fun removeEmptyDaysExceptToday() {
            days = days.filter { it.items.isNotEmpty() || it.date == LocalDate.now() }.toMutableList()
        }

        fun getJson(): String {
            return gson.toJson(days)
        }

        @Throws(Exception::class)
        fun loadBackup(jsonString: String) {
            val mutableListTutorialType = object : TypeToken<MutableList<Day>>() {}.type
            days = gson.fromJson(jsonString, mutableListTutorialType)
        }

        fun saveData(context: Context) {
            var sharedPreferences: SharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            removeEmptyDaysExceptToday()
            val daysString = getJson()
            editor.putString("days", daysString)
            editor.apply()
        }

        fun loadDays(context: Context) {
            var sharedPreferences: SharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val daysString = sharedPreferences.getString("days", "")
            if (daysString == "") {
                days.clear()
            } else if (daysString != null) {
                days.clear()
                val mutableListTutorialType = object : TypeToken<MutableList<Day>>() {}.type
                days = gson.fromJson(daysString, mutableListTutorialType)
            }
        }

        fun loadItems(context: Context) {
            // get json file content from /raw/nutritionvalues.json
            val jsonString = context.resources.openRawResource(R.raw.nutritionvalues).bufferedReader().readText()
            val mutableListTutorialType = object : TypeToken<MutableList<Item>>() {}.type
            additionalItems = gson.fromJson<MutableList<Item>>(jsonString, mutableListTutorialType)
        }

        fun loadData(context: Context) {
            loadDays(context)
            loadItems(context)
        }
    }
}