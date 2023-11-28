package com.pascalrieder.proteincounter.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDate


class DataProvider {
    companion object {
        private var days = mutableListOf<Day>(
            Day(
                1,
                LocalDate.now(),
                mutableListOf(
                    Item(1, "Egg", 12f, 100f),
                    Item(2, "Bread", 10f, 200f)
                )
            ),
            Day(
                2,
                LocalDate.now().minusDays(2),
                mutableListOf(
                    Item(3, "Shake", 14f, 100f),
                    Item(4, "Milk", 10f, 200f)
                )
            )
        )

        public fun addItemToToday(item: Item) {
            var day = days.find { it.date == LocalDate.now() }
            if (day == null) {
                days.add(Day(0, LocalDate.now(), mutableListOf(item)))
            } else {
                day.items.add(item)
            }
        }

        public fun getItems(date: LocalDate): List<Item> {
            return days.find { it.date == date }?.items ?: listOf()
        }

        public fun getItems(): List<Item> {
            // Does not return mutlible items with the same name and proteinContentPercentage
            val items = mutableListOf<Item>()
            days.forEach { day ->
                day.items.forEach { item ->
                    if (!items.any { it.name == item.name && it.proteinContentPercentage == item.proteinContentPercentage }) {
                        items.add(item)
                    }
                }
            }
            return items
        }

        public fun getDays(): List<Day> {
            return days
        }

        fun getTodayConsumedProtein(): Float {
            var protein = 0f
            days.find { it.date == LocalDate.now() }?.items?.forEach { item ->
                protein += item.amountInGramm * item.proteinContentPercentage / 100
            }
            return protein

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