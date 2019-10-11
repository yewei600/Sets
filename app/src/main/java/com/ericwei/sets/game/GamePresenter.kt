package com.ericwei.sets.game

import android.util.Log
import android.widget.Toast
import com.ericwei.sets.model.ColorType
import com.ericwei.sets.model.FillType
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeType

class GamePresenter(private var gameView: GameContract.View) : GameContract.Presenter {

    private val tag = "GamePresenter"
    private var mGameArray: ArrayList<Shape> = arrayListOf()
    private var mCurrentSets: ArrayList<Array<Int>> = arrayListOf()

    override fun startTimer() {
        Log.d("GamePresenter", "here")
    }

    override fun getGameShapes() {
        while (mCurrentSets.size < 2) {
            mGameArray.clear()
            mCurrentSets.clear()
            for (i in 1..9) {
                mGameArray.add(
                    Shape(
                        ShapeType.values().random(),
                        ColorType.values().random(),
                        FillType.values().random()
                    )
                )
            }
            findNumSets()
        }
        var setsStr = ""
        mCurrentSets.forEach {
            setsStr += "(${it[0] + 1} ${it[1] + 1} ${it[2] + 1})\n"
        }
        Log.d(tag, setsStr)
        gameView.onGameShapesReceived(mGameArray)
    }

    private fun findNumSets() {
        for (i in 0..8) {
            for (j in i + 1..8) {
                for (k in j + 1..8) {
                    if (checkIsSet(mGameArray[i], mGameArray[j], mGameArray[k])) {
                        mCurrentSets.add(arrayOf(i, j, k))
                    }
                }
            }
        }
    }

    override fun checkUserPlay() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkIsSet(s1: Shape, s2: Shape, s3: Shape): Boolean {
        val setArray = arrayOf(s1, s2, s3)
        val shapes = mutableSetOf<ShapeType>()
        val colors = mutableSetOf<ColorType>()
        val fills = mutableSetOf<FillType>()

        setArray.forEach { shape ->
            shapes.add(shape.shapeType)
            colors.add(shape.colorType)
            fills.add(shape.fillType)
        }
        return (shapes.size == 1 || shapes.size == 3) &&
                (colors.size == 1 || colors.size == 3) &&
                (fills.size == 1 || fills.size == 3)
    }

}