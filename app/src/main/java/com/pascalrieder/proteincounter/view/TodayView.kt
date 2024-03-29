package com.pascalrieder.proteincounter.view

import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.R
import com.pascalrieder.proteincounter.viewmodel.TodayViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.pascalrieder.proteincounter.SearchAlgorithm
import com.pascalrieder.proteincounter.database.dto.ItemFromDay
import com.pascalrieder.proteincounter.database.models.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayView(viewModel: TodayViewModel) {

    // DayWithItems
    val dayWithItems by viewModel.dayWithItems.observeAsState()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (viewModel.openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.openBottomSheet = false },
            sheetState = bottomSheetState,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_scale),
                        contentDescription = "Search Icon"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Consumed amount in gram") },
                        value = viewModel.amountInGram,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            if (it.isEmpty()) viewModel.amountInGram = ""
                            else if (viewModel.isFloat(it)) viewModel.amountInGram = it
                        })
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search Icon"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search") },
                        value = viewModel.searchText,
                        onValueChange = { viewModel.searchText = it })
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (viewModel.errorMessage.isNotEmpty())
                    Text(text = viewModel.errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(10.dp))


                // Items
                val items by viewModel.items.observeAsState()

                var searchItems = items ?: listOf()
                if (viewModel.searchText.isNotEmpty())
                    searchItems =
                        items?.let { SearchAlgorithm(it).search(viewModel.searchText) } ?: listOf()

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(searchItems) { item ->
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                            ),
                            onClick = { viewModel.insertItemClick(item.uid) },
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                Text(
                                    style = MaterialTheme.typography.headlineSmall,
                                    text = item.name,
                                    maxLines = 2,
                                    modifier = Modifier.width(400.dp)
                                )
                                Text(
                                    style = MaterialTheme.typography.bodyMedium,
                                    text = String.format("%.1f", item.proteinContentPercentage)
                                        .replace(".0", "") + "g",
                                    modifier = Modifier.alpha(0.5f)
                                )
                                Text(
                                    style = MaterialTheme.typography.bodyMedium,
                                    text = String.format("%.1f", item.kcalContentIn100g)
                                        .replace(".0", "") + " kcal",
                                    modifier = Modifier.alpha(0.5f)
                                )

                            }
                        }
                        Spacer (modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    var openAlertDialogEditKcal by remember { mutableStateOf(false) }
    when {
        openAlertDialogEditKcal -> {
            AlertDialog(onDismissRequest = { openAlertDialogEditKcal = false }, confirmButton = {
                TextButton(onClick = {
                    viewModel.onSetKcalGoalClick()
                    openAlertDialogEditKcal = false
                }) {
                    Text("Confirm")
                }
            }, title = {
                Text(text = "Update kcal goal")
            }, text = {
                Column {
                    OutlinedTextField(
                        label = { Text(text = "Kcal goal") },
                        value = viewModel.dialogKcalGoal,
                        onValueChange = viewModel::updateDialogKcalGoal,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openAlertDialogEditKcal = false
                }) {
                    Text("Dismiss")
                }
            })
        }
    }

    var openAlertDialogEditProtein by remember { mutableStateOf(false) }
    when {
        openAlertDialogEditProtein -> {
            AlertDialog(onDismissRequest = { openAlertDialogEditProtein = false }, confirmButton = {
                TextButton(onClick = {
                    viewModel.onSetProteinGoalClick()
                    openAlertDialogEditProtein = false
                }) {
                    Text("Confirm")
                }
            }, title = {
                Text(text = "Update protein goal")
            }, text = {
                Column {
                    OutlinedTextField(
                        label = { Text(text = "Protein goal") },
                        value = viewModel.dialogProteinGoal,
                        onValueChange = viewModel::updateDialogProteinGoal,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openAlertDialogEditProtein = false
                }) {
                    Text("Dismiss")
                }
            })
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Today", style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = dayWithItems?.getFormattedDate() ?: "Day not found",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.alpha(0.5f)
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                NutrientItem(modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.ic_grocery),
                    title = {
                        Text(
                            style = MaterialTheme.typography.titleMedium, text = "Protein"
                        )
                    },
                    text = {
                        Text(
                            buildAnnotatedString {
                                append("You've consumed \n")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(
                                        String.format("%.0f", dayWithItems?.getProteinTotal())
                                            .replace(".0", "")
                                    )
                                }
                                append(" of ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(viewModel.proteinGoal.toString())
                                }
                                append(" g Protein.")
                            }, style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onEdit = {
                        openAlertDialogEditProtein = true
                    })
                Spacer(modifier = Modifier.width(16.dp))



                NutrientItem(modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.ic_lunch_dining),
                    title = { Text(style = MaterialTheme.typography.titleMedium, text = "Kcal") },
                    text = {
                        Text(
                            buildAnnotatedString {
                                append("You've consumed \n")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(
                                        String.format("%.0f", dayWithItems?.getKcalTotal())
                                            .replace(".0", "")
                                    )
                                }
                                append(" of ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(viewModel.kcalGoal.toString())
                                }
                                append(" kcal.")
                            }, style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onEdit = {
                        openAlertDialogEditKcal = true
                    })
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(dayWithItems?.items ?: listOf()) { item ->
                    ItemView(item, onDelete = {
                        viewModel.removeItemFromToday(item)
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun NutrientItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    painter: Painter,
    onEdit: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.shapes.medium
            )
            .padding(12.dp)
            .then(modifier)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Icon(
                painter = painter, contentDescription = "Info Icon", modifier = Modifier.size(24.dp)
            )
            IconButton(modifier = Modifier.size(24.dp), onClick = onEdit) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit",
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        title()
        Spacer(modifier = Modifier.height(4.dp))
        text()
    }
}

@Composable
fun ItemView(item: ItemFromDay, onDelete: () -> Unit = {}) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                MaterialTheme.shapes.extraLarge
            )
            .padding(24.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.headlineSmall, text = item.name
                )
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    text = String.format("%.1f", item.amountInGram).replace(".0", "") + "g",
                    modifier = Modifier.alpha(0.5f)
                )
            }
            IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand Icon"
                )
            }
        }
        if (isExpanded) Spacer(modifier = Modifier.height(32.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isExpanded) CalculationGraph(item.amountInGram, item.proteinContentPercentage)
            else Spacer(modifier = Modifier.width(1.dp))
            Text(
                style = MaterialTheme.typography.titleMedium, text = String.format(
                    "%.1f", (item.amountInGram * item.proteinContentPercentage / 100)
                ).replace(".0", "") + "g"
            )
        }

        if (isExpanded) Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isExpanded) CalculationGraph(item.amountInGram, item.kcalContentIn100g)
            else Spacer(modifier = Modifier.width(1.dp))
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = String.format("%.0f", (item.amountInGram * item.kcalContentIn100g / 100))
                    .replace(".0", "") + " kcal"
            )
        }
        if (isExpanded) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .clickable {
                    onDelete()
                }
                .height(40.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete Icon"
                )
                Spacer(modifier = Modifier.width(13.dp))
                Text(
                    style = MaterialTheme.typography.bodyMedium, text = "Delete"
                )
            }
        }
    }
}

@Composable
fun CalculationGraph(factor: Float, dividend: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            style = MaterialTheme.typography.bodyMedium,
            text = String.format("%.1f", factor).replace(".0", "") + "g",
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
                text = String.format("%.1f", dividend).replace(".0", "") + "g"
            )
            Divider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp,
                modifier = Modifier
                    .width(30.dp)
                    .padding(top = 3.dp)
            )
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = "100g",
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            style = MaterialTheme.typography.bodyMedium, text = "="
        )
    }
}

