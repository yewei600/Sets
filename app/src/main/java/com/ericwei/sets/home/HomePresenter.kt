package com.ericwei.sets.home

import com.ericwei.sets.model.*

class HomePresenter(private var homeView: HomeContract.View) : HomeContract.Presenter {

    private var mShapes: Array<Shape>? = null

    override fun updateShapes() {
        if (mShapes.isNullOrEmpty()) {
            mShapes = arrayOf(
                Shape(ShapeType.TRIANGLE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),
                Shape(ShapeType.SQUARE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
                Shape(ShapeType.CIRCLE, ColorType.BLUE, FillType.FILL, DrawState.UNSELECT)
            )
        }
        homeView.onShapesReceived(mShapes!!)
    }
}