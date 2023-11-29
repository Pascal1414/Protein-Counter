package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.data.Item

@Composable
fun ItemsView() {
    var items = remember { mutableStateListOf<Item>() }
    items.clear()
    items.addAll(DataProvider.getItems())

    Column {
        Text(
            text = "Items",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(128.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                items(items) { item ->
                    Item(item = item)
                }
            },
            modifier = Modifier.fillMaxSize().padding(24.dp)
        )
    }
}

@Composable
fun Item(item: Item) {
    var expanded = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium)
            .padding(24.dp)
            .fillMaxWidth().then(
                if (expanded.value) Modifier.height(200.dp) else Modifier.height(100.dp)
            )
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(
                onClick = { expanded.value = !expanded.value },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Star",
                    modifier = Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
        Text(text = "${item.proteinContentPercentage} % Protein", style = MaterialTheme.typography.bodyMedium)
    }
}