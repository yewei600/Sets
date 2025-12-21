package com.ericwei.sets.rules

import androidx.lifecycle.ViewModel
import com.ericwei.sets.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class RulesUiState(
    val validShapes: Array<Shape> = emptyArray(),
    val invalidShapes: Array<Shape> = emptyArray()
)

@HiltViewModel
class RulesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState: StateFlow<RulesUiState> = _uiState.asStateFlow()

    fun getShapesForRulesPage() {
        val validShapes = arrayOf(
            Shape(ShapeType.CIRCLE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.RED, FillType.EMPTY, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.RED, FillType.LINES, DrawState.UNSELECT),

            Shape(ShapeType.SQUARE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.TRIANGLE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),

            Shape(ShapeType.CIRCLE, ColorType.BLUE, FillType.LINES, DrawState.UNSELECT),
            Shape(ShapeType.TRIANGLE, ColorType.GREEN, FillType.EMPTY, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.RED, FillType.FILL, DrawState.UNSELECT)
        )

        val invalidShapes = arrayOf(
            Shape(ShapeType.CIRCLE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.RED, FillType.EMPTY, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),

            Shape(ShapeType.SQUARE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.CIRCLE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
            Shape(ShapeType.TRIANGLE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),

            Shape(ShapeType.CIRCLE, ColorType.BLUE, FillType.LINES, DrawState.UNSELECT),
            Shape(ShapeType.TRIANGLE, ColorType.GREEN, FillType.LINES, DrawState.UNSELECT),
            Shape(ShapeType.SQUARE, ColorType.RED, FillType.FILL, DrawState.UNSELECT)
        )
        
        _uiState.value = RulesUiState(validShapes, invalidShapes)
    }
}
