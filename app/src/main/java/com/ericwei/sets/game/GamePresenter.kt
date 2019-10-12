package com.ericwei.sets.game

import android.os.Handler
import android.util.Log
import com.ericwei.sets.model.*
import com.ericwei.sets.model.ShapeView.*

class GamePresenter(private var gameView: GameContract.View) : GameContract.Presenter {

    private val TAG = "GamePresenter"
    private var mGameArray = arrayOfNulls<Shape>(9)
    private var mCurrentSets: ArrayList<Array<Int>> = arrayListOf()
    private var mUserSelected: MutableSet<ShapeView> = mutableSetOf()

    override fun startTimer() {
        Log.d("GamePresenter", "here")
    }

    override fun getGameShapes(shapeIds: Array<Int>) {
        while (mCurrentSets.size < 2) {
            //mGameArray.clear()
            mCurrentSets.clear()
            for (i in shapeIds) {
                mGameArray[i] = Shape(
                    ShapeType.values().random(),
                    ColorType.values().random(),
                    FillType.values().random(),
                    DrawState.REDRAW
                )
            }
            findNumSets()
        }
        var setsStr = ""
        mCurrentSets.forEach {
            setsStr += "(${it[0] + 1} ${it[1] + 1} ${it[2] + 1})\n"
        }
        Log.d(TAG, setsStr)
        val updateMap = mutableMapOf<Int, Shape?>()
        for (id in shapeIds) {
            updateMap.put(id,mGameArray[id])
        }
        gameView.onUpdateShapes(updateMap)
        gameView.onCurrentSetsReceived(mCurrentSets)
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
        if (mUserSelected.contains(shapeView)) {
            shapeView.redrawShape(DrawState.UNSELECT)
            mUserSelected.remove(shapeView)

        } else {
            shapeView.redrawShape(DrawState.SELECT)
            mUserSelected.add(shapeView)
            if (mUserSelected.size == 3) {
                Handler().postDelayed({
                    if (checkUserSelectedIsSet()) {
                        //Toast.makeText(this, "SET FOUND!!!", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "SET FOUND!!!")
//                        mUserSelected.forEach { shape ->
//                            shape.redrawShape(DrawState.REDRAW)
//                        }
                    } else {
                        //Toast.makeText(this, "not a set :(", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "not a set :(")
//                        mUserSelected.forEach { shape ->
//                            shape.redrawShape(DrawState.UNSELECT)
//                        }
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
        return checkIsSet(userSet[0], userSet[1], userSet[2])
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