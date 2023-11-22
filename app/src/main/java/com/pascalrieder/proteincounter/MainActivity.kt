package com.pascalrieder.proteincounter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                    Scaffold(content = {
                        Navigation()
                    }, bottomBar = {
                        BottomBar()
                    })

                }
            }
        }
    }


}

@Composable
private fun BottomBar() {
    val selectedIndex = remember { mutableStateOf(0) }

    BottomNavigation(elevation = 10.dp) {
        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Home, "")
        }, label = { Text(text = "Today") }, onClick = {}, selected = (selectedIndex.value == 0)
        )
        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Home, "")
        }, label = { Text(text = "History") }, onClick = {}, selected = (selectedIndex.value == 0)
        )
        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Home, "")
        }, label = { Text(text = "Items") }, onClick = {}, selected = (selectedIndex.value == 0)
        )
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = "today", modifier = Modifier.fillMaxSize()
    ) {
        composable("today") {
            Greeting("Android")
        }
        composable("history") {
            Greeting("Android")
        }
        composable("items") {
            Greeting("Android")
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