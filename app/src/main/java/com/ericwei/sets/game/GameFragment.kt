package com.ericwei.sets.game

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentGameBinding
import com.ericwei.sets.model.DrawState
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView

class GameFragment : Fragment(), GameContract.View {

    private lateinit var mGamePresenter: GameContract.Presenter
    private lateinit var mGridLayout: GridLayout
    private lateinit var mShapeArray: Array<ShapeView>
    private lateinit var mScoreTv: TextView
    private lateinit var mCurSetsTv: TextView
    private lateinit var mTimerTv: TextView

    private val mTimer = object : CountDownTimer(60000 * 2, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val minutes = (millisUntilFinished / 1000) / 60
            val seconds = (millisUntilFinished / 1000) % 60
            mTimerTv.text = "$minutes:$seconds"
        }

        override fun onFinish() {
            this@GameFragment.findNavController()
                .navigate(R.id.action_gameFragment_to_summaryFragment)
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
        mCurSetsTv = binding.curSetsTv
        mTimerTv = binding.timeTv
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
        mShapeArray.forEach { shape ->
            shape.setOnClickListener {
                mGamePresenter.shapeClicked(shape)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mGamePresenter.updateGameShapes(Array(9) { it }, DrawState.REDRAW)
        mTimer.start()
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

    override fun onCurrentSetsReceived(curSets: ArrayList<Array<Int>>) {
        var str = ""
        curSets.forEach { set ->
            str += "(${set[0] + 1} ${set[1] + 1} ${set[2] + 1})  "
        }
        mCurSetsTv.text = str
    }

    override fun onScoreUpdateReceived(score: Int) {
        mScoreTv.text = score.toString()
    }

    override fun updateShapes() {

    }

    override fun onTimerExpired() {

    }
}
