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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pascalrieder.proteincounter.data.LocalDateAdapter
import com.pascalrieder.proteincounter.database.AppDatabase
import com.pascalrieder.proteincounter.database.models.Item
import com.pascalrieder.proteincounter.repository.ItemRepository
import com.pascalrieder.proteincounter.view.HistoryView
import com.pascalrieder.proteincounter.view.ItemsView
import com.pascalrieder.proteincounter.view.TodayView
import com.pascalrieder.proteincounter.viewmodel.HistoryViewModel
import com.pascalrieder.proteincounter.viewmodel.ItemsViewModel
import com.pascalrieder.proteincounter.viewmodel.TodayViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        insertItems()

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
                        FloatingActionButton(onClick = {
                            if (navController.currentBackStackEntry?.destination?.route == "today")
                                todayViewModel.onFabClick()
                            else if (navController.currentBackStackEntry?.destination?.route == "history")
                                historyViewModel.onFabClick()
                            else if (navController.currentBackStackEntry?.destination?.route == "items")
                                itemsViewModel.onFabClick()
                        },
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

    private val gson =
        GsonBuilder().registerTypeAdapter(LocalDate::class.java, LocalDateAdapter()).create()

    fun insertItems() {
        // Get repo
        val itemDao = AppDatabase.getDatabase(application).itemDao()
        val itemRepo = ItemRepository(itemDao)

        // Check if items are already inserted
        itemRepo.readAllData.observe(this@MainActivity) { items ->
            if (items.isEmpty()) {
                // Get items from resources
                val jsonString =
                    resources.openRawResource(R.raw.nutritionvalues).bufferedReader().readText()
                val mutableListTutorialType = object : TypeToken<MutableList<Item>>() {}.type
                val additionalItems =
                    gson.fromJson<MutableList<Item>>(jsonString, mutableListTutorialType)

                // Insert items into database
                additionalItems.forEach {
                    lifecycleScope.launch(Dispatchers.IO) {

                        itemRepo.addItem(it)
                    }
                }
            }
        }
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

