package com.pascalrieder.proteincounter.ui

import android.os.Handler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.FloatingActionButtonHandler
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.data.Item
import kotlinx.coroutines.delay
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

    // Radio Buttons
    val radioOptions = listOf("NewItem", "ExistingItem")
    var (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[1]) }

    // Text Fields
    val name = remember { mutableStateOf("") }
    val proteinPercentage = remember { mutableStateOf("") }
    val amountInGramm = remember { mutableStateOf("") }

    // Dropdown
    var mSelectedItem by remember { mutableStateOf<Item?>(null) }

    // Items
    var items = remember { mutableStateListOf<Item>() }
    items.clear()
    items.addAll(DataProvider.getItems(LocalDate.now()))

    val openAlertDialogCreate = remember { mutableStateOf(false) }
    when {
        openAlertDialogCreate.value -> {
            AlertDialog(icon = {
                Icon(Icons.Default.Create, contentDescription = "Info Icon")
            }, title = {
                Text(text = "Add Item")
            }, text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            RadioButton(selected = (selectedOption == "NewItem"),
                                onClick = { onOptionSelected("NewItem") })
                            Text(text = "New Item")
                        }
                        Column {
                            RadioButton(selected = (selectedOption == "ExistingItem"),
                                onClick = { onOptionSelected("ExistingItem") })
                            Text(text = "Select Item")
                        }
                    }
                    Divider(
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                    if (selectedOption == "NewItem") Column {
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

                    if (selectedOption == "ExistingItem") Column {
                        var mExpanded by remember { mutableStateOf(false) }
                        OutlinedTextField(label = { Text(text = "Select Item") }, value = mSelectedItem?.name ?: "",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    mExpanded = true
                                }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Info Icon")
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
            }, onDismissRequest = {
                openAlertDialogCreate.value = false
            }, confirmButton = {
                TextButton(onClick = {
                    if (amountInGramm.value.isEmpty()) {
                        displayErrorMessage("Please enter an amount")
                        return@TextButton
                    }

                    if (selectedOption == "ExistingItem") {
                        if (mSelectedItem == null) {
                            displayErrorMessage("Please select an Item")
                            return@TextButton
                        }
                        DataProvider.addItemToToday(
                            Item(
                                0,
                                name = mSelectedItem!!.name,
                                proteinContentPercentage = mSelectedItem!!.proteinContentPercentage,
                                amountInGramm = amountInGramm.value.toFloat()
                            )
                        )
                        openAlertDialogCreate.value = false
                    } else {
                        if (name.value.isEmpty()) {
                            displayErrorMessage("Please enter a name")
                            return@TextButton
                        }
                        if (proteinPercentage.value.isEmpty()) {
                            displayErrorMessage("Please enter a protein percentage")
                            return@TextButton
                        }
                        DataProvider.addItemToToday(
                            Item(0, name = name.value, proteinPercentage.value.toFloat(), amountInGramm.value.toFloat())
                        )
                    }
                    openAlertDialogCreate.value = false
                }) {
                    Text("Confirm")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openAlertDialogCreate.value = false
                }) {
                    Text("Dismiss")
                }
            })
        }
    }
    FloatingActionButtonHandler.onClick = {
        openAlertDialogCreate.value = true
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
                    .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(15)).padding(24.dp)
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
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                items.forEach {
                    ItemView(it, onDelete = {
                        DataProvider.removeItemFromToday(it)
                        items.clear()
                        items.addAll(DataProvider.getItems(LocalDate.now()))
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
            .height(if (isExpanded) 245.dp else 100.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(15.dp)).padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 140.dp),
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
                        text = item.amountInGramm.toString() + "g ",
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
                            text = String.format("%.0f", item.amountInGramm) + "g",
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
            }.padding(vertical = 10.dp, horizontal = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(17.dp))
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
