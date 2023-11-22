package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayView() {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}