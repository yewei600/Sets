package com.ericwei.sets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ericwei.sets.game.GameScreen
import com.ericwei.sets.game.GameViewModel
import com.ericwei.sets.home.HomeScreen
import com.ericwei.sets.home.HomeViewModel
import com.ericwei.sets.rules.RulesScreen
import com.ericwei.sets.rules.RulesViewModel
import com.ericwei.sets.ui.theme.SetsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetsApp()
                }
            }
        }
    }

    companion object {
        const val FULL_TIME: Long = 60000 * 2
    }
}

@Composable
fun SetsApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onPlayClicked = { navController.navigate("game") },
                onRulesClicked = { navController.navigate("rules") }
            )
        }
        composable("game") {
            val viewModel: GameViewModel = hiltViewModel()
            GameScreen(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() },
                onGameOver = { score ->
                    navController.navigate("gameOver/$score")
                }
            )
        }
        composable("rules") {
            val viewModel: RulesViewModel = hiltViewModel()
            RulesScreen(
                viewModel = viewModel,
                onBackClicked = { navController.popBackStack() }
            )
        }
        composable(
            "gameOver/{score}",
            arguments = listOf(navArgument("score") { type = NavType.StringType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score") ?: "0"
            // Simple Dialog or Screen for Game Over
            GameOverDialog(
                score = score,
                onPlayAgain = {
                    navController.popBackStack("game", inclusive = true)
                    navController.navigate("game")
                },
                onExit = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}

@Composable
fun GameOverDialog(score: String, onPlayAgain: () -> Unit, onExit: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onExit,
        title = { androidx.compose.material3.Text("Game Finished") },
        text = { androidx.compose.material3.Text("You have earned a score of $score points!") },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onPlayAgain) {
                androidx.compose.material3.Text("Play Again")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onExit) {
                androidx.compose.material3.Text("Exit")
            }
        }
    )
}
