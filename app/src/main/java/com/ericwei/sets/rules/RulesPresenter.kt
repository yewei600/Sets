package com.ericwei.sets.rules

import com.ericwei.sets.model.*

class RulesPresenter(private var rulesView: RulesContract.View) : RulesContract.Presenter {

    override fun getShapesForRulesPage() {
        val validShapes = arrayOf(
            Shape(ShapeType.TRIANGLE, ColorType.RED, FillType.EMPTY, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.RED, FillType.LINES, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.BLUE, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.GREEN, FillType.EMPTY, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.BLUE, FillType.LINES, DrawState.UNSELECT)
        )
        val invalidShapes = arrayOf(
            Shape(ShapeType.TRIANGLE, ColorType.BLUE, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.BLUE, FillType.LINES, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.GREEN, FillType.EMPTY, DrawState.UNSELECT),
            Shape(ShapeType.TRIANGLE, ColorType.BLUE, FillType.LINES, DrawState.UNSELECT),
            Shape(ShapeType.TRIANGLE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.TRIANGLE, ColorType.RED, FillType.LINES, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.TRIANGLE, ColorType.RED, FillType.EMPTY, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.BLUE, FillType.LINES, DrawState.UNSELECT)
        )
        rulesView.onRulesShapesReceived(validShapes, true)
        rulesView.onRulesShapesReceived(invalidShapes, false)
    }
}