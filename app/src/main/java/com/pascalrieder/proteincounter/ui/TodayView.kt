package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.data.Item
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayView() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 50.dp).fillMaxWidth()) {
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Consumed today",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            DataProvider.getItems(LocalDate.now()).forEach {
                ItemView(it)
            }
        }
    }
}

@Composable
fun ItemView(item: Item) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)
            .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(15))
    ) {
        val padding = 15.dp
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = padding, bottom = 5.dp, start = padding, end = padding)
        ) {
            Text(style = MaterialTheme.typography.titleMedium, text = item.amountInGramm.toString() + "g " + item.name)
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = ((item.amountInGramm * (item.proteinContentPercentage / 100))).toString() + "g"
            )
        }
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = item.proteinContentPercentage.toString() + "%",
            modifier = Modifier.padding(end = padding, bottom = padding, start = padding)
        )
    }
}