package com.ericwei.sets.game

import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView


class GameContract {

    interface Presenter {
        fun startTimer()

        fun getGameShapes(shapeIds: Array<Int>)

        fun shapeClicked(shapeView: ShapeView)

        fun checkIsSet(s1: Shape?, s2: Shape?, s3: Shape?): Boolean
    }

    interface View {

        fun onUpdateShapes(updateShapes: Map<Int, Shape?>)

        fun onCurrentSetsReceived(curSets: ArrayList<Array<Int>>)

        fun updateShapes()

        fun onTimerExpired()
    }

}