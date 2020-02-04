package com.ericwei.sets.game

import com.ericwei.sets.model.DrawState
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView


class GameContract {

    interface Presenter {
        fun setView(view: View)

        suspend fun updateGameShapes(shapeIds: Array<Int>, drawState: DrawState)

        suspend fun shapeClicked(shapeView: ShapeView)

        suspend fun checkIsSet(s1: Shape?, s2: Shape?, s3: Shape?): Boolean

        suspend fun getHint()

        suspend fun onHintShapeViewReceived(shapeView: ShapeView)

        suspend fun saveTimeRemaining(remainTime: Long)

        suspend fun loadGameTime()
    }

    interface View {

        fun onShapesUpdateReceived(updateShapes: Map<Int, Shape?>)

        fun onNumPossibleSetsReceived(numSets: Int)

        fun onScoreUpdateReceived(score: Int)

        fun onNumSetsFoundReceived(numSets: Int)

        fun getHintShapeView(shapeId: Int)

        fun playSound(soundId: GameFragment.SOUNDS)

        fun startCountDownTimer(timeMillis: Long)

        fun updateGridClickStatus(isClickable: Boolean)
    }

}