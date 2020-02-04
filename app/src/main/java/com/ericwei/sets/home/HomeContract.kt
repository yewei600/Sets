package com.ericwei.sets.home

import com.ericwei.sets.model.Shape

class HomeContract {

    interface Presenter {
        fun setView(view: View)

        suspend fun updateShapes()
    }

    interface View {
        fun onShapesReceived(shapes: Array<Shape>)
    }

}