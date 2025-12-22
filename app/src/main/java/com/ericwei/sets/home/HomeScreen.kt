package com.ericwei.sets.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwei.sets.ui.components.ShapeComposable

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPlayClicked: () -> Unit,
    onRulesClicked: () -> Unit
) {
    val shapes by viewModel.shapes.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.updateShapes()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SETS",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            shapes.forEach { shape ->
                ShapeComposable(
                    shape = shape,
                    modifier = Modifier.weight(1f),
                    drawFrame = false
                )
            }
        }

        Button(
            onClick = onPlayClicked,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("PLAY")
        }

        Button(
            onClick = onRulesClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("RULES")
        }
    }
}
