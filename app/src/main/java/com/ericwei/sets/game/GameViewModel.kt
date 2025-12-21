package com.ericwei.sets.game

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ericwei.sets.MainActivity
import com.ericwei.sets.R
import com.ericwei.sets.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val shapes: Map<Int, Shape?> = emptyMap(),
    val score: Int = 0,
    val numSetsFound: Int = 0,
    val numPossibleSets: Int = 0,
    val timerMillis: Long = MainActivity.FULL_TIME,
    val isGridClickable: Boolean = true,
    val soundToPlay: GameFragment.SOUNDS? = null,
    val hintShapeId: Int? = null,
    val gameEnded: Boolean = false
)

@HiltViewModel
class GameViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var mGameArray = arrayOfNulls<Shape>(9)
    private var mCurrentSets: ArrayList<Array<Int>> = arrayListOf()
    private val mUserSelected: MutableSet<Int> = mutableSetOf()
    
    private val mSharedPrefs: SharedPreferences =
        context.getSharedPreferences("sets_prefs", Context.MODE_PRIVATE)

    private val ALL_SAME_SCORE = 10
    private val ALL_DIFF_SCORE = 20

    fun initGame() {
        viewModelScope.launch {
            updateGameShapes((0..8).toList().toTypedArray(), DrawState.REDRAW)
            loadGameTime()
        }
    }

    suspend fun updateGameShapes(shapeIds: Array<Int>, drawState: DrawState) {
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
                    mGameArray[i]?.drawState = DrawState.SELECT
                }
            }
            DrawState.UNSELECT -> {
                for (i in shapeIds) {
                    mGameArray[i]?.drawState = DrawState.UNSELECT
                }
            }
        }

        val updateMap = mGameArray.mapIndexed { index, shape -> index to shape }.toMap()
        
        _uiState.update { currentState ->
            currentState.copy(
                shapes = updateMap,
                numPossibleSets = if (drawState == DrawState.REDRAW) mCurrentSets.size else currentState.numPossibleSets,
                isGridClickable = mUserSelected.size < 3
            )
        }
    }

    fun shapeClicked(shapeId: Int) {
        viewModelScope.launch {
            if (mUserSelected.contains(shapeId)) {
                updateGameShapes(arrayOf(shapeId), DrawState.UNSELECT)
                mUserSelected.remove(shapeId)
                _uiState.update { it.copy(soundToPlay = GameFragment.SOUNDS.CLICK_OFF) }
            } else {
                updateGameShapes(arrayOf(shapeId), DrawState.SELECT)
                mUserSelected.add(shapeId)
                _uiState.update { it.copy(soundToPlay = GameFragment.SOUNDS.CLICK_ON) }
                
                if (mUserSelected.size == 3) {
                    val userSetIds = mUserSelected.toTypedArray()
                    _uiState.update { it.copy(isGridClickable = false) }
                    delay(500)
                    if (checkUserSelectedIsSet()) {
                        updateGameShapes(userSetIds, DrawState.REDRAW)
                        _uiState.update { it.copy(soundToPlay = GameFragment.SOUNDS.SET) }
                    } else {
                        updateGameShapes(userSetIds, DrawState.UNSELECT)
                        _uiState.update { it.copy(soundToPlay = GameFragment.SOUNDS.CLICK_OFF) }
                    }
                    mUserSelected.clear()
                    _uiState.update { it.copy(isGridClickable = true) }
                }
            }
            // Reset sound after emitting
            _uiState.update { it.copy(soundToPlay = null) }
        }
    }

    private fun checkUserSelectedIsSet(): Boolean {
        val userSet = mUserSelected.map { mGameArray[it] }
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
            val scoreGain = (if (shapes.size == 1) ALL_SAME_SCORE else ALL_DIFF_SCORE) +
                    (if (colors.size == 1) ALL_SAME_SCORE else ALL_DIFF_SCORE) +
                    (if (fills.size == 1) ALL_SAME_SCORE else ALL_DIFF_SCORE)
            
            _uiState.update { it.copy(
                score = it.score + scoreGain,
                numSetsFound = it.numSetsFound + 1
            )}
        }
        return isSet
    }

    private fun checkIsSet(s1: Shape?, s2: Shape?, s3: Shape?): Boolean {
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

    fun getHint() {
        viewModelScope.launch {
            if (mUserSelected.isNotEmpty()) {
                val clearSelected = mUserSelected.toTypedArray()
                mUserSelected.clear()
                updateGameShapes(clearSelected, DrawState.UNSELECT)
            }
            if (mCurrentSets.isNotEmpty()) {
                val randomSetElement = mCurrentSets.random().random()
                updateGameShapes(arrayOf(randomSetElement), DrawState.SELECT)
                mUserSelected.add(randomSetElement)
                _uiState.update { it.copy(soundToPlay = GameFragment.SOUNDS.CLICK_ON) }
                _uiState.update { it.copy(soundToPlay = null) }
            }
        }
    }

    fun saveTimeRemaining(remainTime: Long) {
        mSharedPrefs.edit().putLong(context.getString(R.string.time_remain), remainTime).apply()
    }

    private fun loadGameTime() {
        val time = mSharedPrefs.getLong(
            context.getString(R.string.time_remain),
            MainActivity.FULL_TIME
        )
        _uiState.update { it.copy(timerMillis = time) }
    }
}
