package com.pascalrieder.proteincounter.data

import java.time.LocalDate

class DataProvider {
    companion object {
        private val days = listOf(
            Day(
                1,
                LocalDate.now(),
                listOf(
                    Item(1, "Egg", 12, 100),
                    Item(2, "Bread", 10, 200)
                )
            ),
            Day(
                2,
                LocalDate.now().minusDays(1),
                listOf(
                    Item(3, "Shake", 14, 100),
                    Item(4, "Milk", 10, 200)
                )
            )
        )

        public fun getItems(date: LocalDate): List<Item> {
            return days.find { it.date == date }?.items ?: listOf()
        }

    }
}