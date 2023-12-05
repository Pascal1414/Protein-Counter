package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    var items = remember { mutableStateListOf<Item>() }
    items.clear()
    items.addAll(DataProvider.getItems())

    Column {
        Text(
            text = "Items",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        // Those are the items that you used before. You can forget them so they don't show up anymore.
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(128.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                items(items) { item ->
                    Box(modifier = Modifier.animateItemPlacement()) {
                        Item(item = item, onDelete = {
                            DataProvider.removeItem(item)
                            items.remove(item)
                        })
                    }
                }
            },
            modifier = Modifier.fillMaxSize().padding(24.dp)
        )
    }
}

@Composable
fun Item(item: Item, onDelete: () -> Unit = {}) {
    var expanded = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium)
            .padding(24.dp)
            .fillMaxWidth().then(
                if (expanded.value) Modifier.height(120.dp) else Modifier.height(80.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
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
        Text(
            text = "${String.format("%.1f", item.proteinContentPercentage).replace(".0", "")}g / 100g Protein",
            style = MaterialTheme.typography.bodyMedium
        )
        if (expanded.value) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp).clickable {
                    onDelete()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Favorite"
                )
                Spacer(modifier = Modifier.width(13.dp))
                Text(text = "Delete", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}