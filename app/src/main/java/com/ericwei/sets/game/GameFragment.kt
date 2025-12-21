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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ericwei.sets.MainActivity
import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentGameBinding
import com.ericwei.sets.game.GameFragment.SOUNDS.*
import com.ericwei.sets.model.ShapeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()
    
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
    private var mTimer: CountDownTimer? = null
    private var mSoundOn: Boolean = true

    enum class SOUNDS(val id: Int) {
        CLICK_ON(0), CLICK_OFF(1), SET(2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )
        assignUi(binding)
        observeViewModel()
        
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
            binding.s1, binding.s2, binding.s3,
            binding.s4, binding.s5, binding.s6,
            binding.s7, binding.s8, binding.s9
        )
        
        mCloseBtn.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_homeFragment)
        }
        
        mHintBtn.setOnClickListener {
            viewModel.getHint()
        }
        
        mSoundBtn.setOnClickListener {
            mSoundOn = !mSoundOn
            mSoundBtn.setImageResource(
                if (mSoundOn) R.drawable.ic_volume_on_24px else R.drawable.ic_volume_off_24px
            )
        }
        
        mShapeArray.forEachIndexed { index, shape ->
            shape.setOnClickListener {
                viewModel.shapeClicked(index)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateShapes(state.shapes)
                    mScoreTv.text = state.score.toString()
                    mNumSetsFoundTv.text = if (state.numSetsFound > 1) "${state.numSetsFound} sets" else "${state.numSetsFound} set"
                    mCurSetsTv.text = "${state.numPossibleSets} sets available"
                    updateGridClickStatus(state.isGridClickable)
                    state.soundToPlay?.let { playSound(it) }
                    
                    if (mTimer == null && state.timerMillis > 0) {
                        startCountDownTimer(state.timerMillis)
                    }
                }
            }
        }
    }

    private fun updateShapes(updateShapes: Map<Int, com.ericwei.sets.model.Shape?>) {
        mShapeArray.forEachIndexed { idx, shapeView ->
            if (idx in updateShapes.keys) {
                shapeView.mShape = updateShapes[idx]
                shapeView.invalidate()
            }
        }
    }

    private fun updateGridClickStatus(isClickable: Boolean) {
        mShapeArray.forEach { shape ->
            shape.isClickable = isClickable
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.initGame()
        mBtnSoundPlayers = arrayOf(
            MediaPlayer.create(context, R.raw.click_on),
            MediaPlayer.create(context, R.raw.click_off),
            MediaPlayer.create(context, R.raw.point)
        )
    }

    override fun onPause() {
        super.onPause()
        mBtnSoundPlayers.forEach { it.release() }
        mTimer?.let {
            // In a real app we'd save the remaining time from the timer state
            // For now, let's assume ViewModel handles persistence if needed
        }
        mTimer?.cancel()
    }

    private fun playSound(soundId: SOUNDS) {
        if (mSoundOn && ::mBtnSoundPlayers.isInitialized) {
            val player = mBtnSoundPlayers[soundId.id]
            player.seekTo(0)
            player.start()
        }
    }

    private fun startCountDownTimer(timeMillis: Long) {
        mTimer?.cancel()
        mTimer = object : CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                viewModel.saveTimeRemaining(millisUntilFinished)
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                mTimerTv.text = String.format("%d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                viewModel.saveTimeRemaining(MainActivity.FULL_TIME)
                findNavController().navigate(
                    R.id.action_gameFragment_to_gameOverDialogFragment,
                    bundleOf(getString(R.string.game_score) to mScoreTv.text)
                )
            }
        }
        mTimer?.start()
    }
}
