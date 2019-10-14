package com.ericwei.sets.game

import android.os.Handler
import android.util.Log
import com.ericwei.sets.model.*

class GamePresenter(private var gameView: GameContract.View) : GameContract.Presenter {

    private val TAG = "GamePresenter"
    private var mScore = 0
    private var mGameArray = arrayOfNulls<Shape>(9)
    private var mCurrentSets: ArrayList<Array<Int>> = arrayListOf()
    private var mUserSelected: MutableSet<ShapeView> = mutableSetOf()
    private val ALL_SAME_SCORE = 10
    private val ALL_DIFF_SCORE = 20

    override fun startTimer() {
        Log.d("GamePresenter", "here")
    }

    override fun updateGameShapes(shapeIds: Array<Int>, drawState: DrawState) {
        when (drawState) {
            DrawState.REDRAW -> {
                Log.d(TAG, "redraw")
                do {
                    mCurrentSets.clear()
                    for (i in shapeIds) {
                        mGameArray[i] = Shape(
                            ShapeType.values().random(),
                            ColorType.values().random(),
                            FillType.values().random(),
                            drawState
                        )
                        Log.d(
                            TAG,
                            "get new  tag=" + i.toString() + "   shape=" + mGameArray[i]?.shapeType + "  color=" + mGameArray[i]?.colorType + "  fill=" + mGameArray[i]?.fillType
                        )
                    }
                    findNumSets()
                } while (mCurrentSets.size < 2 || mCurrentSets.size > 4)
            }
            DrawState.SELECT -> {
                Log.d(TAG, "select")
                for (i in shapeIds) {
                    mGameArray[i]!!.drawState = DrawState.SELECT
                }
            }
            DrawState.UNSELECT -> {
                Log.d(TAG, "unselect")
                for (i in shapeIds) {
                    mGameArray[i]!!.drawState = DrawState.UNSELECT
                }
            }
        }

        val updateMap = mutableMapOf<Int, Shape?>()
        for (id in shapeIds) {
            updateMap[id] = mGameArray[id]
        }
        gameView.onShapesUpdateReceived(updateMap)
        if (drawState == DrawState.REDRAW) {
            gameView.onCurrentSetsReceived(mCurrentSets)
        }
    }

    override fun getHint() {
        if (mUserSelected.size > 0) {
            val clearSelected = Array(mUserSelected.size) { 0 }
            mUserSelected.forEachIndexed { i, shapeView ->
                clearSelected[i] = shapeView.tag.toString().toInt()
            }
            mUserSelected.clear()
            updateGameShapes(clearSelected, DrawState.UNSELECT)
        }
        val randomSetElement = mCurrentSets.random().random()
        updateGameShapes(arrayOf(randomSetElement), DrawState.SELECT)
        gameView.getHintShapeView(randomSetElement)
    }

    override fun onHintShapeViewReceived(shapeView: ShapeView) {
        mUserSelected.add(shapeView)
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

    override fun shapeClicked(shapeView: ShapeView) {
        val viewId = shapeView.tag.toString().toInt()
        if (mUserSelected.contains(shapeView)) {
            updateGameShapes(arrayOf(viewId), DrawState.UNSELECT)
            mUserSelected.remove(shapeView)

        } else {
            updateGameShapes(arrayOf(viewId), DrawState.SELECT)
            mUserSelected.add(shapeView)
            if (mUserSelected.size == 3) {
                val userSet = Array(3) { 0 }
                mUserSelected.forEachIndexed { i, shapeView ->
                    userSet[i] = shapeView.tag.toString().toInt()
                }
                Handler().postDelayed({
                    if (checkUserSelectedIsSet()) {
                        //Toast.makeText(this, "SET FOUND!!!", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "SET FOUND!!!")
                        updateGameShapes(userSet, DrawState.REDRAW)
                    } else {
                        //Toast.makeText(this, "not a set :(", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "not a set :(")
                        updateGameShapes(userSet, DrawState.UNSELECT)

                    }
                    mUserSelected.clear()
                }, 300)
            }
        }
    }

    private fun checkUserSelectedIsSet(): Boolean {
        var userSet = arrayListOf<Shape?>()
        mUserSelected.forEach {
            userSet.add(it.mShape)
        }
        val isSet = checkIsSet(userSet[0], userSet[1], userSet[2])
        if (isSet) {
            val shapes = mutableSetOf<ShapeType?>()
            val colors = mutableSetOf<ColorType?>()
            val fills = mutableSetOf<FillType?>()
            userSet.forEach { shape ->
                shapes.add(shape?.shapeType)
                colors.add(shape?.colorType)
                fills.add(shape?.fillType)
            }
            mScore += if (shapes.size == 1) ALL_SAME_SCORE else ALL_DIFF_SCORE
            mScore += if (colors.size == 1) ALL_SAME_SCORE else ALL_DIFF_SCORE
            mScore += if (fills.size == 1) ALL_SAME_SCORE else ALL_DIFF_SCORE
            gameView.onScoreUpdateReceived(mScore)
        }
        return isSet
    }


    override fun checkIsSet(s1: Shape?, s2: Shape?, s3: Shape?): Boolean {
        val setArray = arrayOf(s1, s2, s3)
        val shapes = mutableSetOf<ShapeType?>()
        val colors = mutableSetOf<ColorType?>()
        val fills = mutableSetOf<FillType?>()

        setArray.forEach { shape ->
            shapes.add(shape?.shapeType)
            colors.add(shape?.colorType)
            fills.add(shape?.fillType)
        }
        return (shapes.size == 1 || shapes.size == 3) &&
                (colors.size == 1 || colors.size == 3) &&
                (fills.size == 1 || fills.size == 3)
    }

}