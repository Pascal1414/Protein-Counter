package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.data.DataProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayView() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()) {
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        DataProvider.getItems(LocalDate.now().minusDays(1)).forEach {
            Text(text = it.name)
        }
    }
}