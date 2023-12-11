package com.pascalrieder.proteincounter.data

class Item(
    val name: String,
    val proteinContentPercentage: Float,
    val kcalContentIn100g: Float,
    val amountInGram: Float,
    var isDeleted: Boolean = false
)