package com.ericwei.sets.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Sets",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 65.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 76.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-110).dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            shapes.take(3).forEach { shape ->
                ShapeComposable(
                    shape = shape,
                    modifier = Modifier.size(120.dp),
                    drawFrame = false
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 156.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LegacyMenuButton(text = "Play", onClick = onPlayClicked)
            Spacer(modifier = Modifier.height(32.dp))
            LegacyMenuButton(text = "Rules", onClick = onRulesClicked)
        }
    }
}

@Composable
private fun LegacyMenuButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 200.dp, height = 80.dp),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF2F2F2),
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}
