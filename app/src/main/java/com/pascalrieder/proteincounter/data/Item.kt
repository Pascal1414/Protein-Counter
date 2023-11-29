package com.pascalrieder.proteincounter.data

class Item(
    val id: Int,
    val name: String,
    val proteinContentPercentage: Float,
    val amountInGramm: Float,
    var isDeleted: Boolean = false
)