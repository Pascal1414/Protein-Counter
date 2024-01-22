package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.m335pascal.database.AppDatabase
import com.m335pascal.database.dto.DayWithItems
import com.m335pascal.repository.DayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

}