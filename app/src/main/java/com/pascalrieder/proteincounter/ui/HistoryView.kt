package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pascalrieder.proteincounter.R
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.data.Day
import java.io.File
import java.time.format.DateTimeFormatter


@Composable
fun HistoryView() {
    var days = remember { mutableStateListOf<Day>() }
    days.clear()
    days.addAll(DataProvider.getDays())

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            var downloadCompleted by remember { mutableStateOf(false) }
            if (downloadCompleted) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1000)
                    downloadCompleted = false
                }
            }
            val context = LocalContext.current
            IconButton(
                onClick = {
                    val (success, fileName) = saveFile()
                    downloadCompleted = success
                }, modifier = Modifier.padding(horizontal = 16.dp), enabled = !downloadCompleted
            ) {
                Icon(
                    painter = if (downloadCompleted) painterResource(R.drawable.ic_download_completed) else painterResource(
                        R.drawable.ic_download
                    ), contentDescription = "Download", tint = MaterialTheme.colorScheme.onSurface
                )
            }

        }
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            days.forEach { day ->
                Column(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), MaterialTheme.shapes.medium
                    ).padding(24.dp)
                ) {
                    Text(
                        text = day.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    day.items.forEach { item ->
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(start = 4.dp)
                        ) {
                            Text(
                                text = String.format("%.1f", item.proteinContentPercentage / 100f * item.amountInGramm)
                                    .replace(".0", "") + "g",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.width(85.dp)
                            )
                            Column {
                                Text(
                                    text = item.name, style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = item.proteinContentPercentage.toString() + "%",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

fun saveFile(): Pair<Boolean, String> {
    try {
        val fileString = DataProvider.getJson()
        val folderDir = File("/storage/emulated/0/Download/ProteinCounter/")
        val f = File(folderDir, "Backup" + System.currentTimeMillis() + ".json")
        f.parentFile.mkdirs()
        f.writeBytes(fileString.toByteArray())
        return Pair(true, f.name)
    } catch (e: Exception) {
        e.printStackTrace()
        return Pair(false, "")
    }
}

