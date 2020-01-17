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
import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentGameBinding
import com.ericwei.sets.game.GameFragment.SOUNDS.*
import com.ericwei.sets.model.DrawState
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView

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
    private var mSoundOn: Boolean = true

    enum class SOUNDS(val id: Int) {
        CLICK_ON(0), CLICK_OFF(1), SET(2)
    }

    private val mTimer = object : CountDownTimer(60000 * 2, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val minutes = (millisUntilFinished / 1000) / 60
            val seconds = (millisUntilFinished / 1000) % 60
            mTimerTv.text = "$minutes:$seconds"
        }

        override fun onFinish() {
            findNavController().navigate(
                R.id.action_gameFragment_to_gameOverDialogFragment,
                bundleOf(getString(R.string.game_score) to mScoreTv.text)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentGameBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )
        mGamePresenter = GamePresenter(this)
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
                mGamePresenter.shapeClicked(shape)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.closeBtn -> findNavController().navigate(R.id.action_gameFragment_to_homeFragment)
            R.id.hintBtn -> mGamePresenter.getHint()
            R.id.soundBtn -> {
                mSoundOn = !mSoundOn
                mSoundBtn.setImageResource(
                    if (mSoundOn) R.drawable.ic_volume_on_24px else
                        R.drawable.ic_volume_off_24px
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mGamePresenter.updateGameShapes(Array(9) { it }, DrawState.REDRAW)
        mBtnSoundPlayers = arrayOf(
            MediaPlayer.create(context, R.raw.click_on),
            MediaPlayer.create(context, R.raw.click_off),
            MediaPlayer.create(context, R.raw.beep)
        )
        mTimer.start()
    }

    override fun onPause() {
        super.onPause()
        mBtnSoundPlayers.forEach {
            it.release()
        }
        mTimer.cancel()
    }

    override fun onShapesUpdateReceived(updateShapes: Map<Int, Shape?>) {
        mGridLayout.isEnabled = false
        for (key in updateShapes.keys) {
            updateShapes[key]?.let { shape ->
                mShapeArray[key].mShape = shape
                mShapeArray[key].invalidate()
            }
        }
        mGridLayout.isEnabled = true
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
        mGamePresenter.onHintShapeViewReceived(mShapeArray[shapeId])
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

    override fun updateShapes() {

    }

    override fun onTimerExpired() {

    }
}
