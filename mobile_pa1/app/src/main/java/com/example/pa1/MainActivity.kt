package com.example.pa1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pa1.motion.CollectScreen
import com.example.pa1.motion.DetectScreen
import com.example.pa1.motion.MotionViewModel
import com.example.pa1.ui.theme.Pa1Theme

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Collect : Screen("collect", "Collect", Icons.Default.List)
    object Detect  : Screen("detect",  "Detect",  Icons.Default.Search)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pa1Theme {
                HarApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarApp() {
    val navController = rememberNavController()

    // ONE shared ViewModel instance for both screens
    val motionViewModel: MotionViewModel = viewModel()

    val items = listOf(Screen.Collect, Screen.Detect)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon     = { Icon(screen.icon, contentDescription = screen.label) },
                        label    = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick  = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Collect.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Collect.route) { CollectScreen(vm = motionViewModel) }
            composable(Screen.Detect.route)  { DetectScreen(vm = motionViewModel) }
        }
    }
}