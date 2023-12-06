package com.pascalrieder.proteincounter

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {
    val snackbarHostState = mutableStateOf(SnackbarHostState())
    suspend fun showSnackbar(message: String, actionLabel: String? = null) {
        snackbarHostState.value.showSnackbar(
            message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long
        )
    }

    var onFloatingActionButtonClick: (() -> Unit)? = null
}