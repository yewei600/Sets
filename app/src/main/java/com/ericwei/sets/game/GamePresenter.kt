package com.ericwei.sets.game

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.ericwei.sets.MainActivity
import com.ericwei.sets.R
import com.ericwei.sets.game.GameFragment.SOUNDS.*
import com.ericwei.sets.model.*
import kotlinx.coroutines.delay

class GamePresenter(private val context: Context) :
    GameContract.Presenter {

    private val TAG = GamePresenter::class.java.simpleName
    private var mGameView: GameContract.View? = null
    private var mScore = 0
    private var mNumSetsFound = 0
    private var mGameArray = arrayOfNulls<Shape>(9)
    private var mCurrentSets: ArrayList<Array<Int>> = arrayListOf()
    private var mUserSelected: MutableSet<ShapeView> = mutableSetOf()
    private var mSharedPrefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val ALL_SAME_SCORE = 10
    private val ALL_DIFF_SCORE = 20

    override fun setView(view: GameContract.View) {
        mGameView = view
    }

    override suspend fun updateGameShapes(shapeIds: Array<Int>, drawState: DrawState) {
        when (drawState) {
            DrawState.REDRAW -> {
                do {
                    mCurrentSets.clear()
                    for (i in shapeIds) {
                        mGameArray[i] = Shape(
                            ShapeType.values().random(),
                            ColorType.values().random(),
                            FillType.values().random(),
                            drawState
                        )
                    }
                    findNumSets()
                } while (mCurrentSets.size < 2 || mCurrentSets.size > 4)
            }
            DrawState.SELECT -> {
                for (i in shapeIds) {
                    mGameArray[i]!!.drawState = DrawState.SELECT
                }
            }
            DrawState.UNSELECT -> {
                for (i in shapeIds) {
                    mGameArray[i]!!.drawState = DrawState.UNSELECT
                }
            }
        }

        val updateMap = mutableMapOf<Int, Shape?>()
        for (id in shapeIds) {
            updateMap[id] = mGameArray[id]
        }
        mGameView?.onShapesUpdateReceived(updateMap)
        if (drawState == DrawState.REDRAW) {
            mGameView?.onNumPossibleSetsReceived(mCurrentSets.size)
        }
    }

    override suspend fun getHint() {
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
        mGameView?.getHintShapeView(randomSetElement)
        mGameView?.playSound(CLICK_ON)
    }

    override suspend fun onHintShapeViewReceived(shapeView: ShapeView) {
        mUserSelected.add(shapeView)
    }

    private suspend fun findNumSets() {
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

    override suspend fun shapeClicked(shapeView: ShapeView) {
        val viewId = shapeView.tag.toString().toInt()
        if (mUserSelected.contains(shapeView)) {
            updateGameShapes(arrayOf(viewId), DrawState.UNSELECT)
            mUserSelected.remove(shapeView)
            mGameView?.playSound(CLICK_OFF)
        } else {
            updateGameShapes(arrayOf(viewId), DrawState.SELECT)
            mUserSelected.add(shapeView)
            mGameView?.playSound(CLICK_ON)
            if (mUserSelected.size == 3) {
                val userSet = Array(3) { 0 }
                mUserSelected.forEachIndexed { i, shapeView ->
                    userSet[i] = shapeView.tag.toString().toInt()
                }
                delay(500)
                if (checkUserSelectedIsSet()) {
                    updateGameShapes(userSet, DrawState.REDRAW)
                    mGameView?.playSound(SET)
                } else {
                    updateGameShapes(userSet, DrawState.UNSELECT)
                    mGameView?.playSound(CLICK_OFF)
                }
                mUserSelected.clear()
            }
        }
    }

    private suspend fun checkUserSelectedIsSet(): Boolean {
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
            mGameView?.onScoreUpdateReceived(mScore)
            mGameView?.onNumSetsFoundReceived(++mNumSetsFound)
        }
        return isSet
    }


    override suspend fun checkIsSet(s1: Shape?, s2: Shape?, s3: Shape?): Boolean {
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

    override suspend fun saveTimeRemaining(remainTime: Long) {
        with(mSharedPrefs.edit()) {
            putLong(context.getString(R.string.time_remain), remainTime)
            apply()
        }
    }

    override suspend fun loadGameTime() {
        mGameView?.startCountDownTimer(
            mSharedPrefs.getLong(
                context.getString(R.string.time_remain),
                MainActivity.FULL_TIME
            )
        )
    }
}