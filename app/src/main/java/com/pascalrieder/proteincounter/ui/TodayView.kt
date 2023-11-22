package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()) {
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        DataProvider.getItems(LocalDate.now().minusDays(1)).forEach {
            ItemView(it)
        }
    }
}

@Composable
fun ItemView(item: Item) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp)
            .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(15))
    ) {
        val padding = 15.dp
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(padding)) {
            Text(style = MaterialTheme.typography.labelLarge, text = item.amountInGramm.toString() + "g " + item.name)
            Text(
                style = MaterialTheme.typography.labelLarge,
                text = ((item.amountInGramm * (item.proteinContentPercentage / 100))).toString() + "g"
            )
        }
        Text(
            style = MaterialTheme.typography.labelLarge,
            text = item.proteinContentPercentage.toString() + "%",
            modifier = Modifier.padding(horizontal = padding).padding(bottom = padding)
        )
    }
}