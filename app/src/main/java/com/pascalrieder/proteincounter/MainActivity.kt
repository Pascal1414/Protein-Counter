package com.pascalrieder.proteincounter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pascalrieder.proteincounter.ui.HistoryView
import com.pascalrieder.proteincounter.ui.ItemsView
import com.pascalrieder.proteincounter.ui.TodayView
import com.pascalrieder.proteincounter.ui.theme.ProteinCounterTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProteinCounterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(content = {
                        Column(modifier = Modifier.padding(top = 16.dp, end = 16.dp, bottom = 55.dp, start = 16.dp)) {
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
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(elevation = 10.dp) {
        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Home, "")
        }, label = { Text(text = "Today") }, onClick = {
            if (selectedIndex.value != 0) {
                navController.navigate("today")
                selectedIndex.value = 0
            }
        }, selected = (selectedIndex.value == 0)
        )
        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Home, "")
        }, label = { Text(text = "History") }, onClick = {
            if (selectedIndex.value != 1) {
                navController.navigate("history")
                selectedIndex.value = 1
            }
        }, selected = (selectedIndex.value == 1)
        )
        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Home, "")
        }, label = { Text(text = "Items") }, onClick = {
            if (selectedIndex.value != 2) {
                navController.navigate("items")
                selectedIndex.value = 2
            }
        }, selected = (selectedIndex.value == 2)
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProteinCounterTheme {
        Greeting("Android")
    }
}