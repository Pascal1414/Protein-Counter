package com.pascalrieder.proteincounter.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
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
import com.pascalrieder.proteincounter.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.time.format.DateTimeFormatter


@Composable
fun HistoryView(historyViewModel: HistoryViewModel) {
    var days by remember { mutableStateOf(DataProvider.getDays()) }

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

            val context = LocalContext.current
            Row {
                var uploadCompleted by remember { mutableStateOf(false) }
                if (uploadCompleted) {
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(1000)
                        uploadCompleted = false
                    }
                }
                val scope = rememberCoroutineScope()
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { activityResult ->
                    if (activityResult.resultCode == Activity.RESULT_OK) {
                        val uri: Uri? = activityResult.data?.data
                        if (uri != null) {
                            val (success, message) = loadFile(context, uri)
                            uploadCompleted = success
                            if (success) days = DataProvider.getDays()
                            scope.launch {
                                historyViewModel.showSnackbar(message, "OK")
                            }
                        }
                    }
                }
                IconButton(
                    onClick = {
                        launcher.launch(android.content.Intent().apply {
                            action = Intent.ACTION_OPEN_DOCUMENT
                            type = "*/*"
                        })
                    }, modifier = Modifier, enabled = !uploadCompleted
                ) {
                    Icon(
                        painter = if (uploadCompleted) painterResource(R.drawable.ic_download_completed) else painterResource(
                            R.drawable.ic_upload_file
                        ),
                        contentDescription = "Download",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                var downloadCompleted by remember { mutableStateOf(false) }
                if (downloadCompleted) {
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(1000)
                        downloadCompleted = false
                    }
                }
                IconButton(
                    onClick = {
                        val (success, fileName) = saveFile()
                        downloadCompleted = success
                        scope.launch {
                            historyViewModel.showSnackbar("Backup Created\n${fileName}", "OK")
                        }
                    }, modifier = Modifier.padding(end = 16.dp), enabled = !downloadCompleted
                ) {
                    Icon(
                        painter = if (downloadCompleted) painterResource(R.drawable.ic_download_completed) else painterResource(
                            R.drawable.ic_file_save
                        ),
                        contentDescription = "Download",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        if (days.isNotEmpty()) LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = 24.dp, end = 24.dp),
            content = {
                items(days) { day ->
                    Column(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                                MaterialTheme.shapes.medium
                            )
                            .padding(24.dp)
                    ) {
                        Text(
                            text = day.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        day.items.forEach { item ->
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp)
                            ) {
                                Text(
                                    text = String.format(
                                        "%.1f", item.amountInGram
                                    ).replace(".0", "") + "g",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.width(85.dp)
                                )
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "${
                                            String.format(
                                                "%.1f",
                                                item.amountInGram * item.proteinContentPercentage / 100
                                            ).replace(".0", "")
                                        }g protein", style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "${
                                            String.format(
                                                "%.1f",
                                                item.amountInGram * item.kcalContentIn100g / 100
                                            )
                                                .replace(".0", "")
                                        } kcal", style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            })
        else Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Past days will appear here",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )
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

fun loadFile(context: Context, uri: Uri): Pair<Boolean, String> {
    // get the file content
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri)
    val fileContent = inputStream?.bufferedReader().use { it?.readText() }
    if (fileContent != null) {
        try {
            DataProvider.loadBackup(fileContent)
            return Pair(true, "Backup loaded")
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, "Invalid backup file")
        }
    }
    return Pair(false, "File could not be read")
}