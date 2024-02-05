package com.pascalrieder.proteincounter

import com.pascalrieder.proteincounter.database.models.Item
import java.util.Locale

class SearchAlgorithm(
    private val items: List<Item>,
) {
    class ItemWithRating(
        val item: Item,
        val rating: Double,
    )

    fun search(query: String): List<Item> {
        val itemsWithRating = mutableListOf<ItemWithRating>()
        items.forEach { item ->
            val nameWithoutBracket = item.name.replace(Regex("\\([^)]*\\)"), "")
                .lowercase(Locale.ROOT)

            val rating = calculatePercentage(nameWithoutBracket, query)

            itemsWithRating.add(ItemWithRating(item, rating))
        }
        return itemsWithRating.filter { it.rating > 0 }.sortedByDescending { it.rating }
            .map { it.item }
    }

    private fun calculatePercentage(inputString: String, searchText: String): Double {
        val occurrences = inputString.lowercase(Locale.ROOT).split(searchText.lowercase(Locale.ROOT)).size - 1
        val totalLength = inputString.length

        return (occurrences.toDouble() / totalLength) * 100
    }
}