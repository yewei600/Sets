package com.ericwei.sets.game

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Buttons (Close, Hint, Sound)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_close_24px),
                    contentDescription = "Close",
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Score
            Text(
                text = "${uiState.score}",
                fontSize = 90.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.weight(0.1f))

            // Timer and Found Count
            Row(
                modifier = Modifier
                    .width(360.dp) // Approximate width of 3x100dp + margins
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.numSetsFound} set",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_timelapse_24px),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = String.format("%d:%02d", (uiState.timerMillis / 1000) / 60, (uiState.timerMillis / 1000) % 60),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            // Grid
            Column(
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                for (row in 0 until 3) {
                    Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                        for (col in 0 until 3) {
                            val index = row * 3 + col
                            ShapeComposable(
                                shape = uiState.shapes[index],
                                modifier = Modifier
                                    .size(120.dp) // 100dp + 10dp margin on each side
                                    .padding(10.dp),
                                onClick = { if (uiState.isGridClickable) viewModel.shapeClicked(index) }
                            )
                        }
                    }
                }
            }

            // Available Sets
            Text(
                text = "${uiState.numPossibleSets} sets available",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(top = 23.dp)
            )

            Spacer(modifier = Modifier.weight(0.32f))
        }

        // Bottom Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { viewModel.getHint() }) {
                Icon(
                    painter = painterResource(R.drawable.ic_help_outline_24px),
                    contentDescription = "Hint",
                    modifier = Modifier.size(50.dp)
                )
            }
            IconButton(onClick = { /* Toggle Sound */ }) {
                Icon(
                    painter = painterResource(R.drawable.ic_volume_on_24px),
                    contentDescription = "Sound",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}
