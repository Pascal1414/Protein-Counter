package com.pascalrieder.proteincounter.data

import com.pascalrieder.proteincounter.database.models.Item
import java.time.LocalDate

class Day (
    val date: LocalDate,
    val items: MutableList<Item>
)