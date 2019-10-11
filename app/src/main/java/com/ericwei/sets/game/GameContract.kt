package com.ericwei.sets.game

import com.ericwei.sets.model.Shape


class GameContract {

    interface Presenter {
        fun startTimer()

        fun getGameShapes()

        fun checkUserPlay()

        fun checkIsSet(s1: Shape, s2: Shape, s3: Shape): Boolean
    }

    interface View {

        fun onGameShapesReceived(gameArray: ArrayList<Shape>)

        fun updateShapes()

        fun onTimerExpired()
    }

}