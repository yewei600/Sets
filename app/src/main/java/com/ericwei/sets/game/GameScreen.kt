package com.ericwei.sets.game

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var soundOn by remember { mutableStateOf(true) }

    val players = remember {
        mapOf(
            GameSound.CLICK_ON to MediaPlayer.create(context, R.raw.click_on),
            GameSound.CLICK_OFF to MediaPlayer.create(context, R.raw.click_off),
            GameSound.SET to MediaPlayer.create(context, R.raw.point)
        )
    }

    LaunchedEffect(Unit) {
        viewModel.initGame()
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            if (soundOn && event is GameEvent.PlaySound) {
                players[event.sound]?.apply {
                    seekTo(0)
                    start()
                }
            }
        }
    }

    LaunchedEffect(uiState.gameEnded) {
        if (uiState.gameEnded) {
            onGameOver(uiState.score.toString())
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveTimeRemaining()
            players.values.forEach { it.release() }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TextMonospace(
            text = uiState.score.toString(),
            fontSize = 90.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )

        LegacyIconButton(
            painterRes = R.drawable.ic_close_24px,
            contentDescription = "Close",
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onBackClicked
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-18).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .width(360.dp)
                    .padding(start = 10.dp, end = 10.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextMonospace(
                    text = if (uiState.numSetsFound == 1) "1 set" else "${uiState.numSetsFound} sets",
                    fontSize = 30.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_timelapse_24px),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    TextMonospace(
                        text = String.format(
                            "%d:%02d",
                            (uiState.timerMillis / 1000) / 60,
                            (uiState.timerMillis / 1000) % 60
                        ),
                        fontSize = 30.sp
                    )
                }
            }

            Column {
                repeat(3) { row ->
                    Row {
                        repeat(3) { col ->
                            val index = row * 3 + col
                            ShapeComposable(
                                shape = uiState.shapes[index],
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(10.dp),
                                onClick = {
                                    if (uiState.isGridClickable) {
                                        viewModel.shapeClicked(index)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            TextMonospace(
                text = "${uiState.numPossibleSets} sets available",
                fontSize = 25.sp,
                modifier = Modifier.padding(top = 23.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 0.dp, bottom = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            LegacyIconButton(
                painterRes = R.drawable.ic_help_outline_24px,
                contentDescription = "Hint",
                onClick = { viewModel.getHint() }
            )
            LegacyIconButton(
                painterRes = if (soundOn) R.drawable.ic_volume_on_24px else R.drawable.ic_volume_off_24px,
                contentDescription = if (soundOn) "Sound On" else "Sound Off",
                onClick = { soundOn = !soundOn }
            )
        }
    }
}

@Composable
private fun TextMonospace(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = Color.Black
    )
}

@Composable
private fun LegacyIconButton(
    painterRes: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(painterRes),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}
