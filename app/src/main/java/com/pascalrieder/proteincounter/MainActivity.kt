package com.pascalrieder.proteincounter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
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
import com.pascalrieder.proteincounter.ui.HistoryView
import com.pascalrieder.proteincounter.ui.ItemsView
import com.pascalrieder.proteincounter.ui.TodayView

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(content = {
                        Column(modifier = Modifier.padding(top = 16.dp, bottom = 70.dp)) {
                            Navigation(navController)
                        }
                    }, bottomBar = {
                        BottomBar(navController)
                    })
                }
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_today),
                    contentDescription = "Today"
                )
            },
            label = { Text("Today") },
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                navController.navigate("today")
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_history),
                    contentDescription = "History"
                )
            },
            label = { Text("History") },
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                navController.navigate("history")
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_list),
                    contentDescription = "Items"
                )
            },
            label = { Text("Items") },
            selected = selectedItem == 2,
            onClick = {
                selectedItem = 2
                navController.navigate("items")
            }
        )
    }
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController, startDestination = "today", modifier = Modifier.fillMaxSize()
    ) {
        composable("today") {
            TodayView()
        }
        composable("history") {
            HistoryView()
        }
        composable("items") {
            ItemsView()
        }
    }
}