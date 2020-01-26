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

        fun getHint()

        fun onHintShapeViewReceived(shapeView: ShapeView)

        fun saveTimeRemaining(remainTime: Long)

        fun loadGameTime()
    }

    interface View {

        fun onShapesUpdateReceived(updateShapes: Map<Int, Shape?>)

        fun onNumPossibleSetsReceived(numSets: Int)

        fun onScoreUpdateReceived(score: Int)

        fun onNumSetsFoundReceived(numSets: Int)

        fun getHintShapeView(shapeId: Int)

        fun playSound(soundId: GameFragment.SOUNDS)

        fun startCountDownTimer(timeMillis: Long)
    }

}