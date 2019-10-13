package com.ericwei.sets.game

import com.ericwei.sets.model.DrawState
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView


class GameContract {

    interface Presenter {
        fun startTimer()

        fun updateGameShapes(shapeIds: Array<Int>, drawState: DrawState)

        fun shapeClicked(shapeView: ShapeView)

        fun checkIsSet(s1: Shape?, s2: Shape?, s3: Shape?): Boolean
    }

    interface View {

        fun onShapesUpdateReceived(updateShapes: Map<Int, Shape?>)

        fun onCurrentSetsReceived(curSets: ArrayList<Array<Int>>)

        fun updateShapes()

        fun onTimerExpired()
    }

}