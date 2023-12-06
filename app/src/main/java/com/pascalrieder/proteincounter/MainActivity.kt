package com.pascalrieder.proteincounter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.pascalrieder.proteincounter.data.DataProvider
import com.pascalrieder.proteincounter.ui.HistoryView
import com.pascalrieder.proteincounter.ui.ItemsView
import com.pascalrieder.proteincounter.ui.TodayView

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataProvider.loadData(context = this)

        val viewModel: AppViewModel by viewModels()
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(content = {
                        Column(modifier = Modifier.padding(top = 16.dp, bottom = 70.dp)) {
                            Navigation(navController, viewModel)
                        }
                    }, bottomBar = {
                        BottomBar(navController)
                    }, snackbarHost = {
                        SnackbarHost(hostState = viewModel.snackbarHostState.value)
                    }, floatingActionButton = {
                        FloatingActionButton(onClick = { viewModel.onFloatingActionButtonClick?.let { it() } },
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

@Composable
fun Navigation(navController: NavHostController, viewModel: AppViewModel) {
    NavHost(
        navController = navController, startDestination = "today", modifier = Modifier.fillMaxSize()
    ) {
        composable("today") {
            TodayView(viewModel)
        }
        composable("history") {
            HistoryView(viewModel)
        }
        composable("items") {
            ItemsView()
        }
    }
}