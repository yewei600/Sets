package com.ericwei.sets.home

import androidx.lifecycle.ViewModel
import com.ericwei.sets.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _shapes = MutableStateFlow<Array<Shape>>(emptyArray())
    val shapes: StateFlow<Array<Shape>> = _shapes.asStateFlow()

    fun updateShapes() {
        if (_shapes.value.isEmpty()) {
            _shapes.value = arrayOf(
                Shape(ShapeType.TRIANGLE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),
                Shape(ShapeType.SQUARE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
                Shape(ShapeType.CIRCLE, ColorType.BLUE, FillType.FILL, DrawState.UNSELECT)
            )
        }
    }
}
