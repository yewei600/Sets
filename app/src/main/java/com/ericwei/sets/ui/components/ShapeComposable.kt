package com.ericwei.sets.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
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
            .clickable(
                enabled = shape != null,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        if (shape != null) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cX = size.width / 2
                val cY = size.height / 2
                val radius = size.width / 3
                val color = Color(shape.colorType.color)
                val frameStrokeWidth = if (shape.drawState == DrawState.SELECT) 40f else 5f
                val shapeStrokeWidth = 7f
                val inset = 5f

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
                        drawPath(path, color = color, style = Stroke(width = shapeStrokeWidth))
                    }

                    FillType.LINES -> {
                        drawPath(path, color = color, style = Stroke(width = shapeStrokeWidth))
                        drawHatchedLines(path, color)
                    }
                }

                if (drawFrame) {
                    drawRect(
                        color = Color.LightGray,
                        topLeft = Offset(inset, inset),
                        size = androidx.compose.ui.geometry.Size(
                            width = size.width - inset * 2,
                            height = size.height - inset * 2
                        ),
                        style = Stroke(width = frameStrokeWidth)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawHatchedLines(path: Path, color: Color) {
    clipPath(path) {
        val step = 10f
        // We use a range that covers the entire rotated area to ensure full coverage
        val range = (size.width + size.height).toInt()
        for (i in -range..range step step.toInt().coerceAtLeast(1)) {
            drawLine(
                color = color,
                start = Offset(i.toFloat(), 0f),
                end = Offset(i.toFloat() - size.height, size.height),
                strokeWidth = 3f
            )
        }
    }
}
