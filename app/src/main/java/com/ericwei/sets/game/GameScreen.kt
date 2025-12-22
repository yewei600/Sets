package com.ericwei.sets.game

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwei.sets.R
import com.ericwei.sets.ui.components.ShapeComposable

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackClicked: () -> Unit,
    onGameOver: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    val players = remember {
        mapOf(
            GameSound.CLICK_ON to MediaPlayer.create(context, R.raw.click_on),
            GameSound.CLICK_OFF to MediaPlayer.create(context, R.raw.click_off),
            GameSound.SET to MediaPlayer.create(context, R.raw.point)
        )
    }

    LaunchedEffect(Unit) {
        viewModel.initGame()
        viewModel.events.collect { event ->
            if (event is GameEvent.PlaySound) {
                players[event.sound]?.apply {
                    seekTo(0)
                    start()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            players.values.forEach { it.release() }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(painter = painterResource(R.drawable.ic_close_24px), contentDescription = "Close")
            }
            Text(text = "Score: ${uiState.score}", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { viewModel.getHint() }) {
                Icon(painter = painterResource(R.drawable.ic_help_outline_24px), contentDescription = "Hint")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Found: ${uiState.numSetsFound}")
            Text(
                text = String.format("%d:%02d", (uiState.timerMillis / 1000) / 60, (uiState.timerMillis / 1000) % 60),
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "${uiState.numPossibleSets} possible")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center
        ) {
            items(9) { index ->
                ShapeComposable(
                    shape = uiState.shapes[index],
                    onClick = { if (uiState.isGridClickable) viewModel.shapeClicked(index) }
                )
            }
        }
    }
}
