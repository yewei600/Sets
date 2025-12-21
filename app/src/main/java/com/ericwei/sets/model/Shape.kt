package com.ericwei.sets.model

import android.graphics.Color

enum class ShapeType(val num: Int) {
    TRIANGLE(0),
    SQUARE(1),
    CIRCLE(2)
}

enum class ColorType(val color: Int) {
    RED(Color.RED),
    GREEN(Color.GREEN),
    BLUE(Color.BLUE)
}

enum class FillType(val num: Int) {
    FILL(0),
    EMPTY(1),
    LINES(2)
}

enum class DrawState {
    REDRAW,
    SELECT,
    UNSELECT
}

data class Shape(
    val shapeType: ShapeType,
    val colorType: ColorType,
    val fillType: FillType,
    var drawState: DrawState
)
