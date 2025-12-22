package com.ericwei.sets.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ericwei.sets.model.DrawState
import com.ericwei.sets.model.FillType
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeType

@Composable
fun ShapeComposable(
    shape: Shape?,
    modifier: Modifier = Modifier,
    drawFrame: Boolean = true,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .then(
                if (drawFrame) {
                    Modifier.border(
                        width = when (shape?.drawState) {
                            DrawState.SELECT -> 8.dp
                            else -> 1.dp
                        },
                        color = when (shape?.drawState) {
                            DrawState.SELECT -> Color.LightGray
                            else -> Color.Transparent
                        }
                    )
                } else Modifier
            )
            .clickable(enabled = shape != null) { onClick() }
    ) {
        if (shape != null) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val cX = size.width / 2
                val cY = size.height / 2
                val radius = size.width / 3
                val color = Color(shape.colorType.color)

                val path = Path()
                when (shape.shapeType) {
                    ShapeType.CIRCLE -> {
                        path.addOval(Rect(cX - radius, cY - radius, cX + radius, cY + radius))
                    }
                    ShapeType.SQUARE -> {
                        path.addRect(Rect(cX - radius, cY - radius, cX + radius, cY + radius))
                    }
                    ShapeType.TRIANGLE -> {
                        path.moveTo(cX - radius, cY + radius)
                        path.lineTo(cX, cY - radius)
                        path.lineTo(cX + radius, cY + radius)
                        path.close()
                    }
                }

                when (shape.fillType) {
                    FillType.FILL -> {
                        drawPath(path, color = color)
                    }
                    FillType.EMPTY -> {
                        drawPath(path, color = color, style = Stroke(width = 4.dp.toPx()))
                    }
                    FillType.LINES -> {
                        drawPath(path, color = color, style = Stroke(width = 4.dp.toPx()))
                        drawLinesInPath(path, color)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawLinesInPath(path: Path, color: Color) {
    // Simplified lines drawing for Compose
    // In a production app, we'd use a more robust path clipping approach
    val step = 10f
    for (i in 0..size.width.toInt() step step.toInt()) {
        drawLine(
            color = color,
            start = Offset(i.toFloat(), 0f),
            end = Offset(i.toFloat(), size.height),
            strokeWidth = 1f
        )
    }
}
