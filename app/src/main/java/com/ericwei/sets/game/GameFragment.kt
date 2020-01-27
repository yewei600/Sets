package com.ericwei.sets.game

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ericwei.sets.MainActivity.Companion.FULL_TIME
import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentGameBinding
import com.ericwei.sets.game.GameFragment.SOUNDS.*
import com.ericwei.sets.model.DrawState
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView
import kotlinx.coroutines.*

class GameFragment : Fragment(), GameContract.View, View.OnClickListener {

    private lateinit var mGamePresenter: GameContract.Presenter
    private lateinit var mGridLayout: GridLayout
    private lateinit var mShapeArray: Array<ShapeView>
    private lateinit var mScoreTv: TextView
    private lateinit var mNumSetsFoundTv: TextView
    private lateinit var mTimerTv: TextView
    private lateinit var mCurSetsTv: TextView
    private lateinit var mCloseBtn: ImageButton
    private lateinit var mHintBtn: ImageButton
    private lateinit var mSoundBtn: ImageButton
    private lateinit var mBtnSoundPlayers: Array<MediaPlayer>
    private lateinit var mTimer: CountDownTimer
    private lateinit var mCoroutineScope: CoroutineScope
    private var mRemainTime: Long? = null
    private var mSoundOn: Boolean = true
    private var mGameInProgress: Boolean = false

    enum class SOUNDS(val id: Int) {
        CLICK_ON(0), CLICK_OFF(1), SET(2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentGameBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )
        mCoroutineScope = CoroutineScope(Dispatchers.Main + Job())
        mGamePresenter = GamePresenter(this, context!!, mCoroutineScope)
        assignUi(binding)

        return binding.root
    }

    private fun assignUi(binding: FragmentGameBinding) {
        mGridLayout = binding.grid
        mScoreTv = binding.scoreTv
        mNumSetsFoundTv = binding.numSetsFoundTv
        mTimerTv = binding.timeTv
        mCurSetsTv = binding.curSetsTv
        mCloseBtn = binding.closeBtn
        mHintBtn = binding.hintBtn
        mSoundBtn = binding.soundBtn
        mShapeArray = arrayOf(
            binding.s1,
            binding.s2,
            binding.s3,
            binding.s4,
            binding.s5,
            binding.s6,
            binding.s7,
            binding.s8,
            binding.s9
        )
        mCloseBtn.setOnClickListener(this)
        mHintBtn.setOnClickListener(this)
        mSoundBtn.setOnClickListener(this)
        mShapeArray.forEach { shape ->
            shape.setOnClickListener {
                mCoroutineScope.launch {
                    mGamePresenter.shapeClicked(shape)
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.closeBtn -> {
                mGameInProgress = false
                mRemainTime = null
                findNavController().navigate(R.id.action_gameFragment_to_homeFragment)
            }
            R.id.hintBtn -> mCoroutineScope.launch { mGamePresenter.getHint() }
            R.id.soundBtn -> {
                mSoundOn = !mSoundOn
                mSoundBtn.setImageResource(
                    if (mSoundOn) R.drawable.ic_volume_on_24px else
                        R.drawable.ic_volume_off_24px
                )
            }
        }
    }

    override fun updateGridClickStatus(isClickable: Boolean) {
        mShapeArray.forEach { shape ->
            shape.isClickable = isClickable
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mGameInProgress) {
            mCoroutineScope.launch {
                mGamePresenter.updateGameShapes(Array(9) { it }, DrawState.REDRAW)
            }
        }
        mCoroutineScope.launch {
            mGamePresenter.loadGameTime()
        }
        mBtnSoundPlayers = arrayOf(
            MediaPlayer.create(context, R.raw.click_on),
            MediaPlayer.create(context, R.raw.click_off),
            MediaPlayer.create(context, R.raw.point)
        )
    }

    override fun onPause() {
        super.onPause()
        mBtnSoundPlayers.forEach {
            it.release()
        }
        mGameInProgress = true
        mCoroutineScope.launch {
            mGamePresenter.saveTimeRemaining(if (mRemainTime != null) mRemainTime!! else FULL_TIME)
        }
        mTimer.cancel()
    }

    override fun onDestroy() {
        mCoroutineScope.cancel()
        super.onDestroy()
    }

    override fun onShapesUpdateReceived(updateShapes: Map<Int, Shape?>) {
        var selectedCnt = 0
        mShapeArray.forEachIndexed { idx, shapeView ->
            if (idx in updateShapes.keys) {
                shapeView.mShape = updateShapes[idx]
                shapeView.invalidate()
            }
            if (shapeView.mShape!!.drawState == DrawState.SELECT) {
                selectedCnt++
            }
        }
        updateGridClickStatus(isClickable = selectedCnt < 3)
    }

    override fun onNumPossibleSetsReceived(numSets: Int) {
        mCurSetsTv.text = "$numSets sets available"
    }

    override fun onScoreUpdateReceived(score: Int) {
        mScoreTv.text = score.toString()
    }

    override fun onNumSetsFoundReceived(numSets: Int) {
        mNumSetsFoundTv.text = if (numSets > 1) "$numSets sets" else "$numSets set"
    }

    override fun getHintShapeView(shapeId: Int) {
        mCoroutineScope.launch { mGamePresenter.onHintShapeViewReceived(mShapeArray[shapeId]) }
    }

    override fun playSound(soundId: SOUNDS) {
        if (mSoundOn) {
            when (soundId) {
                CLICK_ON -> {
                    mBtnSoundPlayers[CLICK_ON.id].seekTo(0)
                    mBtnSoundPlayers[CLICK_ON.id].start()
                }
                CLICK_OFF -> {
                    mBtnSoundPlayers[CLICK_OFF.id].seekTo(0)
                    mBtnSoundPlayers[CLICK_OFF.id].start()
                }
                SET -> {
                    mBtnSoundPlayers[SET.id].seekTo(0)
                    mBtnSoundPlayers[SET.id].start()
                }
            }
        }
    }

    override fun startCountDownTimer(timeMillis: Long) {
        mTimer = object : CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mRemainTime = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                if (seconds < 10) {
                    mTimerTv.text = "$minutes:0$seconds"
                } else {
                    mTimerTv.text = "$minutes:$seconds"
                }

            }

            override fun onFinish() {
                mCoroutineScope.launch {
                    mGamePresenter.saveTimeRemaining(FULL_TIME)
                }
                mGameInProgress = false
                findNavController().navigate(
                    R.id.action_gameFragment_to_gameOverDialogFragment,
                    bundleOf(getString(R.string.game_score) to mScoreTv.text)
                )
            }
        }
        mTimer.start()
    }
}
