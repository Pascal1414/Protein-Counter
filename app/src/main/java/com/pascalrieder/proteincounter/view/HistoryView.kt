package com.pascalrieder.proteincounter.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pascalrieder.proteincounter.R
import com.pascalrieder.proteincounter.database.models.Day
import com.pascalrieder.proteincounter.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.time.format.DateTimeFormatter


@Composable
fun HistoryView(viewModel: HistoryViewModel) {
    val days by viewModel.daysWithItems.observeAsState(emptyList())
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    when {
        viewModel.openAlertDialog.value ->
            AlertDialog(onDismissRequest = { }, title = {
                Text(text = "Backup loaded")
            }, text = {
                Text(text = "The app needs to be restarted to apply the changes.")
            }, confirmButton = {
                TextButton(
                    onClick = {
                        activity.finish()
                    }
                ) {
                    Text("Close App")
                }
            })
    }

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
            var uploadCompleted by remember { mutableStateOf(false) }
            if (uploadCompleted) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1000)
                    uploadCompleted = false
                }
            }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = activityResult.data?.data
                    if (uri != null)
                        viewModel.loadBackup(uri, context)
                }
            }


            Row {
                IconButton(
                    onClick = {
                        viewModel.createBackup(context)
                    }, modifier = Modifier
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_file_save
                        ),
                        contentDescription = "Download",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = {
                        launcher.launch(
                            Intent.createChooser(
                                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "application/zip"
                                },
                                "Select a backup"
                            )
                        )
                    }, modifier = Modifier.padding(end = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_upload_file
                        ),
                        contentDescription = "Load",
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
                                        }g protein",
                                        style = MaterialTheme.typography.bodySmall
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
/*
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
}*/