package com.ericwei.sets.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericwei.sets.model.Shape
import com.ericwei.sets.ui.components.ShapeComposable

@Suppress("UNUSED_PARAMETER")
@Composable
fun RulesScreen(
    viewModel: RulesViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getShapesForRulesPage()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RulesHeader("How to play", top = 20.dp)
        RulesBodyText(
            text = "The objective of the game is to identify 'sets' of 3 cards. Each card is unique in its 3 features: color, shape, and shading. A 'set' consists of 3 cards on which each feature is either the same on all 3 cards, or different on all 3 cards.",
            modifier = Modifier.padding(start = 10.dp, top = 15.dp, end = 10.dp)
        )

        RulesHeader("Scoring", top = 30.dp)
        RulesBodyText(
            text = "The player gets 10 points for each feature that's all the same and 20 points for each feature that's all different. The score for each 'set' is the sum of points for all 3 features.",
            modifier = Modifier.padding(start = 10.dp, top = 15.dp, end = 10.dp)
        )

        RulesHeader("Valid Sets", top = 30.dp)
        RulesGrid(shapes = uiState.validShapes.toList(), modifier = Modifier.padding(top = 0.dp))

        RulesHeader("Invalid Sets", top = 5.dp)
        RulesGrid(shapes = uiState.invalidShapes.take(3), columns = 3)
        RulesCaption("(Two of the cards are blue)")
        RulesGrid(shapes = uiState.invalidShapes.drop(3).take(3), columns = 3)
        RulesCaption("(Two of the cards are striped)")
        RulesGrid(shapes = uiState.invalidShapes.drop(6).take(3), columns = 3)
        RulesCaption("(Two of the cards are squares)")
    }
}

@Composable
private fun RulesHeader(text: String, top: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        modifier = Modifier.padding(top = top),
        fontFamily = FontFamily.Monospace,
        fontSize = 30.sp,
        color = Color.Black,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
private fun RulesBodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        fontFamily = FontFamily.Monospace,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Composable
private fun RulesCaption(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(top = 0.dp),
        fontFamily = FontFamily.Monospace,
        fontSize = 20.sp,
        color = Color.Black
    )
}

@Composable
private fun RulesGrid(
    shapes: List<Shape>,
    columns: Int = 3,
    modifier: Modifier = Modifier
) {
    val rows = if (shapes.isEmpty()) 0 else (shapes.size + columns - 1) / columns
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(rows) { rowIndex ->
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(columns) { colIndex ->
                    val idx = rowIndex * columns + colIndex
                    if (idx < shapes.size) {
                        ShapeComposable(
                            shape = shapes[idx],
                            modifier = Modifier
                                .size(120.dp)
                                .padding(10.dp),
                            drawFrame = false
                        )
                    }
                }
            }
        }
    }
}
