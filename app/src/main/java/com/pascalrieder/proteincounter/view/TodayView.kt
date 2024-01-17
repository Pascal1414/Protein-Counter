package com.pascalrieder.proteincounter.view

import android.os.Handler
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.R
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.data.Item
import com.pascalrieder.proteincounter.viewmodel.TodayViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayView(viewModel: TodayViewModel) {
    val errorMessage = remember { mutableStateOf("") }
    fun displayErrorMessage(message: String) {
        errorMessage.value = message
        val handler = Handler()
        handler.postDelayed({
            errorMessage.value = ""
        }, 3000)
    }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var amountInGram by remember { mutableStateOf("") }
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
                        value = amountInGram,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            if (it.isEmpty()) amountInGram = ""
                            else if (isFloat(it)) amountInGram = it
                        })
                }
                var searchText by remember { mutableStateOf("") }
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
                        value = searchText,
                        onValueChange = { searchText = it })
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = errorMessage.value, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(10.dp))


                val searchItems by remember { mutableStateOf(DataProvider.getItems()) }
                val context = LocalContext.current
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(searchItems.filter {
                        it.name.contains(
                            searchText, ignoreCase = true
                        )
                    }) { item ->
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                MaterialTheme.shapes.extraLarge
                            )
                            .padding(24.dp)
                            .clickable {
                                if (amountInGram.isEmpty()) {
                                    displayErrorMessage("Please enter an amount")
                                    return@clickable
                                }
                                val item = Item(
                                    name = item.name,
                                    proteinContentPercentage = item.proteinContentPercentage,
                                    amountInGram = amountInGram.toFloat(),
                                    kcalContentIn100g = item.kcalContentIn100g
                                )
                                DataProvider.addItemToTodayAndCreateBackupIfNeeded(
                                    item, context
                                )
                                items = DataProvider.getItems(LocalDate.now())
                                openBottomSheet = false
                            }) {
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
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
    viewModel.onFloatingActionButtonClick = {
        openBottomSheet = true
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Today", style = MaterialTheme.typography.displayLarge
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
                modifier = Modifier.fillMaxWidth()
            ) {
                val consumedProtein =
                    String.format("%.1f", DataProvider.getTodayConsumedProtein()).replace(".0", "")
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
                                append("You've consumed ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(consumedProtein)
                                }
                                append("g of Protein today")
                            }, style = MaterialTheme.typography.bodyMedium
                        )
                    })
                Spacer(modifier = Modifier.width(16.dp))
                val consumedKcal =
                    String.format("%.1f", DataProvider.getTodayConsumedKcal()).replace(".0", "")
                NutrientItem(modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.ic_lunch_dining),
                    title = { Text(style = MaterialTheme.typography.titleMedium, text = "Kcal") },
                    text = {
                        Text(
                            buildAnnotatedString {
                                append("You've consumed ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(consumedKcal)
                                }
                                append(" kcal today")
                            }, style = MaterialTheme.typography.bodyMedium
                        )
                    })
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
fun NutrientItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    painter: Painter
) {
    Column(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.shapes.medium
            )
            .padding(12.dp)
            .then(modifier)
    ) {
        Icon(
            painter = painter, contentDescription = "Info Icon", modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        title()
        Spacer(modifier = Modifier.height(4.dp))
        text()
    }
}

@Composable
fun ItemView(item: Item, onDelete: () -> Unit = {}) {
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

fun isFloat(str: String): Boolean {
    return try {
        str.toFloat()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
