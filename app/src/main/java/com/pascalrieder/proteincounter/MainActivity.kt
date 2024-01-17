package com.pascalrieder.proteincounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.view.HistoryView
import com.pascalrieder.proteincounter.view.ItemsView
import com.pascalrieder.proteincounter.view.TodayView
import com.pascalrieder.proteincounter.viewmodel.HistoryViewModel
import com.pascalrieder.proteincounter.viewmodel.ItemsViewModel
import com.pascalrieder.proteincounter.viewmodel.TodayViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataProvider.loadData(context = this)

        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val todayViewModel = ViewModelProvider(this).get(TodayViewModel::class.java)
                    val historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
                    val itemsViewModel = ViewModelProvider(this).get(ItemsViewModel::class.java)

                    Scaffold(content = { padding ->
                        Column(
                            modifier = Modifier
                                .padding(padding)
                                .padding(top = 16.dp)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "today",
                                modifier = Modifier.fillMaxSize()
                            ) {
                                composable("today") {
                                    TodayView(todayViewModel)
                                }
                                composable("history") {
                                    HistoryView(historyViewModel)
                                }
                                composable("items") {
                                    ItemsView(itemsViewModel)
                                }
                            }
                        }
                    }, bottomBar = {
                        BottomBar(navController)
                    }, snackbarHost = {
                        SnackbarHost(hostState = historyViewModel.snackbarHostState.value)
                    }, floatingActionButton = {
                        FloatingActionButton(onClick = { todayViewModel.onFloatingActionButtonClick?.let { it() } },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Add, contentDescription = "Add Icon"
                                )
                            })
                    })
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        DataProvider.saveData(context = this)
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    NavigationBar {
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(R.drawable.ic_today), contentDescription = "Today"
            )
        }, label = { Text("Today") }, selected = selectedItem == 0, onClick = {
            selectedItem = 0
            navController.navigate("today")
        })
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(R.drawable.ic_history), contentDescription = "History"
            )
        }, label = { Text("History") }, selected = selectedItem == 1, onClick = {
            selectedItem = 1
            navController.navigate("history")
        })
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(R.drawable.ic_list), contentDescription = "Items"
            )
        }, label = { Text("Items") }, selected = selectedItem == 2, onClick = {
            selectedItem = 2
            navController.navigate("items")
        })
    }
}

