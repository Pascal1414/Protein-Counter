package com.pascalrieder.proteincounter.data

import java.time.LocalDate

class Day (
    val id: Int,
    val date: LocalDate,
    val items: MutableList<Item>
)