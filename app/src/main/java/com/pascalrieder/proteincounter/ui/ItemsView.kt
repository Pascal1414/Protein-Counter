package com.pascalrieder.proteincounter.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pascalrieder.proteincounter.data.DataProvider

@Composable
fun ItemsView() {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        DataProvider.getItems().forEach {
            Text(text = it.name + " (" + it.proteinContentPercentage + "%)")
        }
    }
}