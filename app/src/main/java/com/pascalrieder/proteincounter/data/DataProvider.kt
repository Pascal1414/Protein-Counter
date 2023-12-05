package com.pascalrieder.proteincounter.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDate


class DataProvider {
    companion object {
        private var days = mutableListOf<Day>()

        public fun addItemToToday(item: Item) {
            var day = days.find { it.date == LocalDate.now() }
            if (day == null) {
                days.add(Day( LocalDate.now(), mutableListOf(item)))
            } else {
                day.items.add(item)
            }
        }

        public fun getItems(date: LocalDate): List<Item> {
            return days.find { it.date == date }?.items?.toList() ?: listOf()
        }

        public fun getItems(): List<Item> {
            // Does not return mutlible items with the same name and proteinContentPercentage
            val items = mutableListOf<Item>()
            days.forEach { day ->
                day.items.forEach { item ->
                    if (!items.any { it.name == item.name && it.proteinContentPercentage == item.proteinContentPercentage} && !item.isDeleted) {
                        items.add(item)
                    }
                }
            }
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

        public fun getDays(): List<Day> {
            return days.sortedByDescending { it.date }
        }

        fun getTodayConsumedProtein(): Float {
            var protein = 0f
            days.find { it.date == LocalDate.now() }?.items?.forEach { item ->
                protein += item.amountInGramm * item.proteinContentPercentage / 100
            }
            return protein
        }

        fun removeItemFromToday(item: Item) {
            var day = days.find { it.date == LocalDate.now() }
            day?.items?.remove(item)
        }

        val gson = GsonBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).create()

        fun saveData(context: Context) {
            var sharedPreferences: SharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val daysString = gson.toJson(days)
            editor.putString("days", daysString)
            editor.apply()
        }

        fun loadData(context: Context) {
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
    }
}