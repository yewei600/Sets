package com.ericwei.sets.game

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ericwei.sets.MainActivity
import com.ericwei.sets.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "sets_prefs")
private val TIME_REMAIN_KEY = longPreferencesKey("time_remain")

data class GameUiState(
    val shapes: Map<Int, Shape?> = emptyMap(),
    val score: Int = 0,
    val numSetsFound: Int = 0,
    val numPossibleSets: Int = 0,
    val timerMillis: Long = MainActivity.FULL_TIME,
    val isGridClickable: Boolean = true,
    val gameEnded: Boolean = false
)

enum class GameSound(val id: Int) {
    CLICK_ON(0), CLICK_OFF(1), SET(2)
}

sealed class GameEvent {
    data class PlaySound(val sound: GameSound) : GameEvent()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>()
    val events = _events.asSharedFlow()

    private var mGameArray = arrayOfNulls<Shape>(9)
    private var mCurrentSets: ArrayList<Array<Int>> = arrayListOf()
    private val mUserSelected: MutableSet<Int> = mutableSetOf()
    private val appContext = getApplication<Application>().applicationContext

    private val ALL_SAME_SCORE = 10
    private val ALL_DIFF_SCORE = 20

    private var timerJob: Job? = null

    fun initGame() {
        viewModelScope.launch {
            // Reset state for new game
            _uiState.update { GameUiState() }
            mUserSelected.clear()
            mGameArray = arrayOfNulls<Shape>(9)

            updateGameShapes((0..8).toList().toTypedArray(), DrawState.REDRAW)
            loadGameTime()

            // If loaded time is 0 or less, reset to FULL_TIME
            if (_uiState.value.timerMillis <= 0) {
                _uiState.update { it.copy(timerMillis = MainActivity.FULL_TIME) }
            }

            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (uiState.value.timerMillis > 0) {
                delay(1000)
                _uiState.update { currentState ->
                    val newTime = (currentState.timerMillis - 1000).coerceAtLeast(0)
                    currentState.copy(timerMillis = newTime)
                }
            }
            _uiState.update { it.copy(gameEnded = true, isGridClickable = false) }
        }
    }

    suspend fun updateGameShapes(shapeIds: Array<Int>, drawState: DrawState) {
        when (drawState) {
            DrawState.REDRAW -> {
                do {
                    mCurrentSets.clear()
                    for (i in shapeIds) {
                        mGameArray[i] = Shape(
                            ShapeType.entries.random(),
                            ColorType.entries.random(),
                            FillType.entries.random(),
                            DrawState.UNSELECT
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

        val updateMap = mGameArray.mapIndexed { index, shape -> index to shape?.copy() }.toMap()

        _uiState.update { currentState ->
            currentState.copy(
                shapes = updateMap,
                numPossibleSets = if (drawState == DrawState.REDRAW) mCurrentSets.size else currentState.numPossibleSets,
                isGridClickable = mUserSelected.size < 3 && !currentState.gameEnded
            )
        }
    }

    fun shapeClicked(shapeId: Int) {
        if (uiState.value.gameEnded) return

        viewModelScope.launch {
            if (mUserSelected.contains(shapeId)) {
                mUserSelected.remove(shapeId)
                updateGameShapes(arrayOf(shapeId), DrawState.UNSELECT)
                _events.emit(GameEvent.PlaySound(GameSound.CLICK_OFF))
            } else {
                mUserSelected.add(shapeId)
                updateGameShapes(arrayOf(shapeId), DrawState.SELECT)
                _events.emit(GameEvent.PlaySound(GameSound.CLICK_ON))

                if (mUserSelected.size == 3) {
                    val userSetIds = mUserSelected.toTypedArray()
                    _uiState.update { it.copy(isGridClickable = false) }
                    delay(500)
                    if (checkUserSelectedIsSet()) {
                        mUserSelected.clear()
                        updateGameShapes(userSetIds, DrawState.REDRAW)
                        _events.emit(GameEvent.PlaySound(GameSound.SET))
                    } else {
                        mUserSelected.clear()
                        updateGameShapes(userSetIds, DrawState.UNSELECT)
                        _events.emit(GameEvent.PlaySound(GameSound.CLICK_OFF))
                    }
                    if (!uiState.value.gameEnded) {
                        _uiState.update { it.copy(isGridClickable = true) }
                    }
                }
            }
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

            _uiState.update {
                it.copy(
                    score = it.score + scoreGain,
                    numSetsFound = it.numSetsFound + 1
                )
            }
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
        if (uiState.value.gameEnded) return

        viewModelScope.launch {
            if (mUserSelected.isNotEmpty()) {
                val clearSelected = mUserSelected.toTypedArray()
                mUserSelected.clear()
                updateGameShapes(clearSelected, DrawState.UNSELECT)
            }
            if (mCurrentSets.isNotEmpty()) {
                val randomSetElement = mCurrentSets.random().random()
                mUserSelected.add(randomSetElement)
                updateGameShapes(arrayOf(randomSetElement), DrawState.SELECT)
                _events.emit(GameEvent.PlaySound(GameSound.CLICK_ON))
            }
        }
    }

    fun saveTimeRemaining() {
        val remainTime = uiState.value.timerMillis
        viewModelScope.launch {
            appContext.dataStore.edit { prefs ->
                prefs[TIME_REMAIN_KEY] = remainTime
            }
        }
    }

    private suspend fun loadGameTime() {
        val time = appContext.dataStore.data.first()[TIME_REMAIN_KEY] ?: MainActivity.FULL_TIME
        _uiState.update { it.copy(timerMillis = time) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
