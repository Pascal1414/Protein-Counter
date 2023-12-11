package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.R
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.data.Item

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsView() {
    var items by remember { mutableStateOf(DataProvider.getItems()) }

    Column {
        Text(
            text = "Items",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        // Those are the items that you used before. You can forget them so they don't show up anymore.
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp), content = {
                items(items) { item ->
                    Item(item = item, onDelete = {
                        DataProvider.removeItem(item)
                        items = items.filter { it != item }
                    })
                }
            }, modifier = Modifier.fillMaxSize().padding(24.dp)
        )
    }
}

@Composable
fun Item(item: Item, onDelete: () -> Unit = {}) {
    var expanded = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.background(
            MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), MaterialTheme.shapes.medium
        ).padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
            IconButton(
                onClick = { expanded.value = !expanded.value }, modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Star",
                    modifier = Modifier
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${String.format("%.1f", item.proteinContentPercentage).replace(".0", "")}g / 100g Protein",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${String.format("%.1f", item.kcalContentIn100g).replace(".0", "")} kcal / 100g ",
            style = MaterialTheme.typography.bodyMedium
        )
        if (expanded.value) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp).clickable {
                    onDelete()
                }, verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete), contentDescription = "Favorite"
                )
                Spacer(modifier = Modifier.width(13.dp))
                Text(text = "Delete", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}