package com.pascalrieder.proteincounter.ui

import android.os.Handler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.FloatingActionButtonHandler
import com.pascalrieder.proteincounter.R
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.data.Item
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayView() {
    val errorMessage = remember { mutableStateOf("") }
    fun displayErrorMessage(message: String) {
        errorMessage.value = message
        val handler = Handler()
        handler.postDelayed({
            errorMessage.value = ""
        }, 3000)
    }


    // Text Fields
    val name = remember { mutableStateOf("") }
    val proteinPercentage = remember { mutableStateOf("") }
    val amountInGramm = remember { mutableStateOf("") }

    // Dropdown
    var mSelectedItem by remember { mutableStateOf<Item?>(null) }

    // Items
    var items by remember { mutableStateOf(DataProvider.getItems(LocalDate.now())) }

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(modifier = Modifier.fillMaxWidth().height(500.dp)) {
                val titles = listOf("Create Item", "Existing Item")
                var state by remember { mutableStateOf(0) }
                PrimaryTabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }, icon = {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Info Icon"
                                )
                            }
                        )
                    }
                }
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    if (state == 0) Column {
                        OutlinedTextField(
                            label = { Text("Name") },
                            value = name.value,
                            onValueChange = { name.value = it })
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(label = { Text(text = "Protein Percentage") },
                            value = proteinPercentage.value,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            onValueChange = {
                                if (it.isEmpty()) proteinPercentage.value = ""
                                else if (isFloat(it) && it.toFloat() in 0f..100f) proteinPercentage.value = it
                            })

                    }

                    if (state == 1) Column {
                        var mExpanded by remember { mutableStateOf(false) }
                        OutlinedTextField(label = { Text(text = "Select Item") }, value = mSelectedItem?.name ?: "",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    mExpanded = true
                                }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Info Icon")
                                }
                            })
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownMenu(
                            expanded = mExpanded,
                            onDismissRequest = { mExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DataProvider.getItems().forEach { item ->
                                DropdownMenuItem(onClick = {
                                    mExpanded = false
                                    mSelectedItem = item
                                }) {
                                    Text(item.name + " (" + item.proteinContentPercentage + "%)")
                                }
                            }


                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(label = { Text(text = "Consumed amount in Gramm") }, value = amountInGramm.value,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            if (it.isEmpty()) amountInGramm.value = ""
                            else if (isFloat(it)) amountInGramm.value = it
                        })
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
                }
                Button(onClick = {
                    if (amountInGramm.value.isEmpty()) {
                        displayErrorMessage("Please enter an amount")
                        return@Button
                    }

                    if (state == 1) {
                        if (mSelectedItem == null) {
                            displayErrorMessage("Please select an Item")
                            return@Button
                        }
                        val newItem = Item(
                            name = mSelectedItem!!.name,
                            proteinContentPercentage = mSelectedItem!!.proteinContentPercentage,
                            amountInGramm = amountInGramm.value.toFloat()
                        )
                        DataProvider.addItemToToday(newItem)
                        items += newItem
                        openBottomSheet = false
                    } else {
                        if (name.value.isEmpty()) {
                            displayErrorMessage("Please enter a name")
                            return@Button
                        }
                        if (proteinPercentage.value.isEmpty()) {
                            displayErrorMessage("Please enter a protein percentage")
                            return@Button
                        }
                        val newItem =
                            Item(name = name.value, proteinPercentage.value.toFloat(), amountInGramm.value.toFloat())
                        DataProvider.addItemToToday(newItem)
                        items += newItem
                    }
                    openBottomSheet = false
                }) {
                    Text("Confirm")
                }
            }

        }
    }

    FloatingActionButtonHandler.onClick = {
        openBottomSheet = true
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.alpha(0.5f)
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.shapes.medium).padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Info Icon",
                    modifier = Modifier.size(24.dp)
                )
                val consumedProtein = String.format("%.1f", DataProvider.getTodayConsumedProtein()).replace(".0", "")
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = "You have consumed " + consumedProtein + "g of protein today",
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(items) { item ->
                    ItemView(item, onDelete = {
                        DataProvider.removeItemFromToday(item)
                        items = DataProvider.getItems(LocalDate.now())
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ItemView(item: Item, onDelete: () -> Unit = {}) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth()
            .animateContentSize()
            .height(if (isExpanded) 225.dp else 100.dp)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), MaterialTheme.shapes.extraLarge)
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 120.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        style = MaterialTheme.typography.headlineSmall,
                        text = item.name
                    )
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text = String.format("%.1f", item.amountInGramm).replace(".0", "") + "g",
                        modifier = Modifier.alpha(0.5f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = String.format("%.1f", item.amountInGramm).replace(".0", "") + "g",
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "×",
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier.wrapContentSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                style = MaterialTheme.typography.bodyMedium,
                                text = String.format("%.1f", item.proteinContentPercentage).replace(".0", "") + "g"
                            )
                            Divider(
                                color = MaterialTheme.colorScheme.primary,
                                thickness = 1.dp,
                                modifier = Modifier.width(30.dp).padding(top = 3.dp)
                            )
                            Text(
                                style = MaterialTheme.typography.bodyMedium,
                                text = "100g",
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "="
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight().then(
                    if (isExpanded)
                        Modifier.padding(bottom = 6.dp)
                    else
                        Modifier.padding(bottom = 0.dp)
                )
            ) {
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.align(Alignment.End).size(15.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Info Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = String.format("%.1f", (item.amountInGramm * item.proteinContentPercentage / 100))
                        .replace(".0", "") + "g"
                )
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onDelete()
            }.height(40.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete Icon"
            )
            Spacer(modifier = Modifier.width(13.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = "Delete"
            )
        }
    }
}

fun isFloat(str: String): Boolean {
    return try {
        str.toFloat()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
