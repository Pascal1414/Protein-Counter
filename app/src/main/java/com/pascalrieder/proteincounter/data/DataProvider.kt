package com.pascalrieder.proteincounter.data

import java.time.LocalDate

class DataProvider {
    companion object {
        private val days = listOf(
            Day(
                1,
                LocalDate.now().minusDays(1),
                listOf(
                    Item(1, "Egg", 12f, 100f),
                    Item(2, "Bread", 10f, 200f)
                )
            ),
            Day(
                2,
                LocalDate.now().minusDays(2),
                listOf(
                    Item(3, "Shake", 14f, 100f),
                    Item(4, "Milk", 10f, 200f)
                )
            )
        )

        public fun getItems(date: LocalDate): List<Item> {
            return days.find { it.date == date }?.items ?: listOf()
        }

        public fun getItems(): List<Item> {
            return days.flatMap { it.items }
        }

        public fun getDays(): List<Day> {
            return days
        }
    }
}