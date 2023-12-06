package com.pascalrieder.proteincounter.data

class Item(
    val name: String,
    val proteinContentPercentage: Float,
    val amountInGram: Float,
    var isDeleted: Boolean = false
)