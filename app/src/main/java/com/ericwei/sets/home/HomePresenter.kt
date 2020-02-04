package com.ericwei.sets.home

import com.ericwei.sets.model.*

class HomePresenter : HomeContract.Presenter {

    private var mHomeView: HomeContract.View? = null
    private var mShapes: Array<Shape>? = null

    override fun setView(view: HomeContract.View) {
        mHomeView = view
    }

    override suspend fun updateShapes() {
        if (mShapes.isNullOrEmpty()) {
            mShapes = arrayOf(
                Shape(ShapeType.TRIANGLE, ColorType.RED, FillType.FILL, DrawState.UNSELECT),
                Shape(ShapeType.SQUARE, ColorType.GREEN, FillType.FILL, DrawState.UNSELECT),
                Shape(ShapeType.CIRCLE, ColorType.BLUE, FillType.FILL, DrawState.UNSELECT)
            )
        }
        mHomeView?.onShapesReceived(mShapes!!)
    }
}