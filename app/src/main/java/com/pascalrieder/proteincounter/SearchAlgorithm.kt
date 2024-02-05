package com.pascalrieder.proteincounter

import androidx.compose.ui.text.toLowerCase
import com.pascalrieder.proteincounter.database.models.Item
import java.util.Locale

class SearchAlgorithm(
    private val items: List<Item>,
) {
    class ItemWithRating(
        val item: Item,
        val rating: Int,
    )

    fun search(query: String): List<Item> {
        val queryWords = query.lowercase(Locale.ROOT).split(" ")
        val itemsWithRating = mutableListOf<ItemWithRating>()
        items.forEach { item ->
            val nameWithoutBracket = item.name.replace(Regex("\\([^)]*\\)"), "")
                .lowercase(Locale.ROOT)

            var rating = 0
            if (nameWithoutBracket.replace(" ", "") == query.replace(" ", "")) rating++
            if (queryWords.any { nameWithoutBracket.startsWith(it, ignoreCase = true) }) rating++
            if (queryWords.any { nameWithoutBracket.contains(it, ignoreCase = true) }) rating++
            if (queryWords.any { nameWithoutBracket.contains(it, ignoreCase = true) }) rating++

            itemsWithRating.add(ItemWithRating(item, rating))
        }
        return itemsWithRating.filter { it.rating > 0 }.sortedByDescending { it.rating }
            .map { it.item }
    }
}