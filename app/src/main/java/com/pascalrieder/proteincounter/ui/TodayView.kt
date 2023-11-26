package com.pascalrieder.proteincounter.ui

import android.os.Handler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
                        Text(text = "Name")
                        OutlinedTextField(value = name.value, onValueChange = { name.value = it })
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Protein Percentage")
                        OutlinedTextField(value = proteinPercentage.value,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            onValueChange = {
                                if (it.isEmpty()) proteinPercentage.value = ""
                                else if (isFloat(it) && it.toFloat() in 0f..100f) proteinPercentage.value = it
                            })

                    }

                    if (selectedOption == "ExistingItem") Column {
                        var mExpanded by remember { mutableStateOf(false) }
                        Text(text = "Select Item")
                        OutlinedTextField(value = mSelectedItem?.name ?: "",
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
                    Text(text = "Consumed amount in Gramm")
                    OutlinedTextField(value = amountInGramm.value,
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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 50.dp).fillMaxWidth()) {
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            openAlertDialogCreate.value = true
        }, content = { Text(text = "Add Item") })

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

fun isFloat(str: String): Boolean {
    return try {
        str.toFloat()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
