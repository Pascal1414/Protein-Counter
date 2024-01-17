package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

class HistoryViewModel (application: Application) : AndroidViewModel(application) {
    val snackbarHostState = mutableStateOf(SnackbarHostState())
    suspend fun showSnackbar(message: String, actionLabel: String? = null) {
        snackbarHostState.value.showSnackbar(
            message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long
        )
    }

}