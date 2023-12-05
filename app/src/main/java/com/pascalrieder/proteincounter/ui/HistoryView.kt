package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.data.Day
import java.time.format.DateTimeFormatter

@Composable
fun HistoryView() {
    var days = remember { mutableStateListOf<Day>() }
    days.clear()
    days.addAll(DataProvider.getDays())

    Column {
        Text(
            text = "History",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            days.forEach { day ->
                Column(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.shapes.medium
                    ).padding(24.dp)
                ) {
                    Text(
                        text = day.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    day.items.forEach { item ->
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(start = 4.dp)) {
                            Text(
                                text = String.format("%.1f", item.proteinContentPercentage / 100f * item.amountInGramm).replace(".0", "")
                                        + "g",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.width(85.dp)
                            )
                            Column {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = item.proteinContentPercentage.toString() + "%",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
