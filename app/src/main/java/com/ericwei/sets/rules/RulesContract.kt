package com.ericwei.sets.rules

import com.ericwei.sets.model.Shape

class RulesContract {

    interface Presenter {
        fun getShapesForRulesPage()
    }

    interface View {
        fun onRulesShapesReceived(shapes: Array<Shape>, forValidArray: Boolean)
    }
}