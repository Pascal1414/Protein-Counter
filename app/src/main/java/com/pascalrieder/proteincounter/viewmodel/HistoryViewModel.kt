package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
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

    val snackbarHostState = mutableStateOf(SnackbarHostState())
    suspend fun showSnackbar(message: String, actionLabel: String? = null) {
        snackbarHostState.value.showSnackbar(
            message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long
        )
    }


    fun loadBackup(uri: Uri) {

    }

    fun createBackup(context: Context) {
        val dbFilePath = getDatabasePath(context)
        val backupName = "DatabaseBackup${System.currentTimeMillis()}"
        val tmpDestinationDirectory =
            "${context.cacheDir.absolutePath}/ProteinCounterBackups/$backupName"

        val destinationDirectory =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/ProteinCounterBackups/"


        val file1Name = "item_database"
        /*val file1TmpPath = "$tmpDestinationDirectory/$file1Name"
        copyFile(File("$dbFilePath/$file1Name"), File(file1TmpPath))*/

        val file2Name = "item_database-shm"
       /* val file2TmpPath = "$tmpDestinationDirectory/$file2Name"
        copyFile(File("$dbFilePath/$file2Name"), File(file2TmpPath))*/

        val file3Name = "item_database-wal"
     /*   val file3TmpPath = "$tmpDestinationDirectory/$file3Name"
        copyFile(File("$dbFilePath/$file3Name"), File(file3TmpPath))*/

        createZipFile(
            destinationDirectory,
            "$backupName.zip",
            listOf("$dbFilePath/$file1Name", "$dbFilePath/$file2Name", "$dbFilePath/$file3Name")
        )

        viewModelScope.launch(Dispatchers.Main) {
            showSnackbar("Backup created. Name: $backupName")
        }
    }

    private fun createZipFile(outputDir: String, outputFileName: String, files: List<String>) {
        val outputFile = File(outputDir, outputFileName)
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

    fun copyFile(srcFile: File, destFile: File) {
        if (!destFile.parentFile?.exists()!!)
            destFile.parentFile?.mkdirs()

        destFile.createNewFile()

        FileInputStream(srcFile).channel.use { sourceChannel ->
            FileOutputStream(destFile).channel
                .use { destinationChannel ->
                    sourceChannel.transferTo(
                        0,
                        sourceChannel.size(),
                        destinationChannel
                    )
                }
        }
    }

    private fun getDatabasePath(context: Context): String {
        return "${context.applicationInfo.dataDir}/databases/"
    }
}