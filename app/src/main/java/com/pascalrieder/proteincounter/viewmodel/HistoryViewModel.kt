package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import android.content.Context
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

    fun createBackup(context: Context) {
        val dbFilePath = getDatabasePath(context)
        val backupName = "DatabaseBackup${System.currentTimeMillis()}"
        val destinationDirectory =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/ProteinCounterBackups/$backupName"

        val file1Name = "item_database"
        copyFile(File("$dbFilePath/$file1Name"), File("$destinationDirectory/$file1Name"))

        val file2Name = "item_database-shm"
        copyFile(File("$dbFilePath/$file2Name"), File("$destinationDirectory/$file2Name"))

        val file3Name = "item_database-wal"
        copyFile(File("$dbFilePath/$file3Name"), File("$destinationDirectory/$file3Name"))

        viewModelScope.launch(Dispatchers.Main) {
            showSnackbar("Backup created. Name: $backupName")
        }
    }

    fun loadBackup() {
        TODO("Not yet implemented")
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