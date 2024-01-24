package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent.getIntent
import android.net.Uri
import android.os.Environment
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.m335pascal.repository.DayRepository
import com.pascalrieder.proteincounter.database.AppDatabase
import com.pascalrieder.proteincounter.database.dto.DayWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    var onFabClick: () -> Unit = {}

    lateinit var daysWithItems: LiveData<List<DayWithItems>>
    private val repository: DayRepository

    init {
        val dayDao = AppDatabase.getDatabase(application).dayDao()
        repository = DayRepository(dayDao)
        viewModelScope.launch(Dispatchers.IO) {
            daysWithItems = repository.getDaysWithItems()
        }
    }

    val openAlertDialog = mutableStateOf(false)

    val snackbarHostState = mutableStateOf(SnackbarHostState())
    suspend fun showSnackbar(message: String, actionLabel: String? = null) {
        snackbarHostState.value.showSnackbar(
            message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long
        )
    }


    fun loadBackup(uri: Uri, context: Context) {
        try {
            val (success, message) = replaceDatabaseFilesWithZipFiles(zipFile = uri, context)
            if (!success) {
                viewModelScope.launch(Dispatchers.Main) {
                    showSnackbar(message)
                }
            } else
                openAlertDialog.value = true
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.Main) {
                showSnackbar("Error loading backup: ${e.message}")
            }
        }
    }

    fun createBackup(context: Context) {
        val dbFilePath = getDatabasePath(context)
        val backupName = "DatabaseBackup${System.currentTimeMillis()}"
        val destinationDirectory =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/ProteinCounterBackups/"

        val file1Name = "item_database"
        val file2Name = "item_database-shm"
        val file3Name = "item_database-wal"

        try {
            createZipFile(
                destinationDirectory,
                "$backupName.zip",
                listOf("$dbFilePath/$file1Name", "$dbFilePath/$file2Name", "$dbFilePath/$file3Name")
            )
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.Main) {
                showSnackbar("Error creating backup: ${e.message}")
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            showSnackbar("Backup created. Name: $backupName")
        }
    }

    val allowedFileNames = listOf("item_database", "item_database-shm", "item_database-wal")
    fun replaceDatabaseFilesWithZipFiles(zipFile: Uri, context: Context): Pair<Boolean, String> {
        val outputDir = File(getDatabasePath(context))

        val documentFile = DocumentFile.fromSingleUri(context, zipFile)
        if (documentFile != null && documentFile.isFile) {
            val filenames = getFileNamesFromZipFile(context, zipFile)
            if (filenames.containsAll(allowedFileNames)) {
                ZipInputStream(context.contentResolver.openInputStream(zipFile)).use { zipStream ->
                    var entry: ZipEntry? = zipStream.nextEntry
                    while (entry != null) {
                        val outputFile = File(outputDir, entry.name)

                        outputFile.parentFile?.mkdirs()

                        FileOutputStream(outputFile).use { output ->
                            zipStream.copyTo(output)
                        }

                        entry = zipStream.nextEntry
                    }
                }
                return Pair(true, "")
            } else
                return Pair(false, "Backup file has an invalid content.")

        }
        return Pair(false, "Invalid backup file.")
    }

    fun getFileNamesFromZipFile(context: Context, zipFile: Uri): List<String> {
        val files = mutableListOf<String>()
        ZipInputStream(context.contentResolver.openInputStream(zipFile)).use { zipStream ->
            var entry: ZipEntry? = zipStream.nextEntry
            while (entry != null) {
                files.add(entry.name)
                entry = zipStream.nextEntry
            }
        }
        return files
    }

    private fun createZipFile(outputDir: String, outputFileName: String, files: List<String>) {
        val outputFile = File(outputDir + outputFileName)
        if (!outputFile.parentFile?.exists()!!)
            outputFile.parentFile?.mkdirs()
        ZipOutputStream(FileOutputStream(outputFile)).use { zipStream ->
            for (fileName in files) {
                val file = File(fileName)
                if (file.exists()) {
                    val entry = ZipEntry(fileName.split("/").last())
                    zipStream.putNextEntry(entry)
                    file.inputStream().use { input ->
                        input.copyTo(zipStream)
                    }
                    zipStream.closeEntry()
                }
            }
        }
    }

    private fun getDatabasePath(context: Context): String {
        return "${context.applicationInfo.dataDir}/databases/"
    }
}