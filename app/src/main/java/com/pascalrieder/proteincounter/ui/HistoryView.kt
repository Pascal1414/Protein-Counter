package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.data.DataProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryView() {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        DataProvider.getDays().forEach {
            Text(text = it.date.toString())
            it.items.forEach {
                Text(it.name)
            }
        }

    }
}