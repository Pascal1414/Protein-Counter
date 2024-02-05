package com.pascalrieder.proteincounter.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.R
import com.pascalrieder.proteincounter.database.models.Item
import com.pascalrieder.proteincounter.viewmodel.ItemsViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ItemsView(viewModel: ItemsViewModel) {
    if (viewModel.openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.openBottomSheet = false },
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_menu), contentDescription = "Menu Icon"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Name") },
                        value = viewModel.createItemName,
                        onValueChange = {
                            viewModel.createItemName = it
                        },
                        isError = viewModel.createItemNameResponse.isNotEmpty()
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_scale), contentDescription = "Menu Icon"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(text = "Protein in 100g") },
                            value = viewModel.createItemProteinContentPercentage,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            onValueChange = {
                                if (it.isEmpty()) viewModel.createItemProteinContentPercentage = ""
                                else if (viewModel.isFloat(it)) viewModel.createItemProteinContentPercentage =
                                    it
                            },
                            isError = viewModel.createItemProteinContentPercentageResponse.isNotEmpty(),
                        )

                    }

                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(36.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Kcal in 100g") },
                        value = viewModel.createItemKcalContentIn100g,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            if (it.isEmpty()) viewModel.createItemKcalContentIn100g = ""
                            else if (viewModel.isFloat(it)) viewModel.createItemKcalContentIn100g =
                                it
                        },
                        isError = viewModel.createItemKcalContentIn100gResponse.isNotEmpty(),
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {
                    viewModel.createItemClick()
                }) {
                    Text(text = "Create")
                }
            }
        }
    }


    val items by viewModel.allItems.observeAsState(emptyList())

    val searchItems = items.filter {
        it.name.contains(viewModel.searchText, ignoreCase = true)
    }

    Column {
        Text(
            text = "Items",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (items.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.shapes.medium
                        )
                        .padding(24.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = "Info Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = "Nutritional values refer to 100g of the product.",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search") },
                    value = viewModel.searchText,
                    onValueChange = { viewModel.searchText = it })

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), content = {
                    items(searchItems) { item ->
                        Item(item = item, onDelete = {
                            viewModel.removeItem(item)
                        })
                    }
                })
            }
        } else Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Your items will appear here",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )
        }
    }
}

@Composable
fun Item(item: Item, onDelete: () -> Unit = {}) {
    var expanded = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), MaterialTheme.shapes.medium
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
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
            text = "${
                String.format("%.1f", item.proteinContentPercentage).replace(".0", "")
            }g Protein", style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${String.format("%.1f", item.kcalContentIn100g).replace(".0", "")} kcal",
            style = MaterialTheme.typography.bodyMedium
        )
        if (expanded.value) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable {
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