package com.ericwei.sets.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentGameBinding
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView

class GameFragment : Fragment(), GameContract.View {

    private lateinit var mGamePresenter: GameContract.Presenter
    private lateinit var mGridLayout: GridLayout
    private lateinit var mShapeArray: Array<ShapeView>
    private lateinit var mCurSetsTv: TextView

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
        mCurSetsTv = binding.curSetsTv
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
        mGamePresenter.startTimer()
        mGamePresenter.getGameShapes()
    }

    override fun onGameShapesReceived(gameArray: ArrayList<Shape>) {
        for (i in 0..8) {
            mShapeArray[i].setShapeAttributes(gameArray[i])
        }
        mGridLayout.invalidate()
    }

    override fun onCurrentSetsReceived(curSets: ArrayList<Array<Int>>) {
        var str = ""
        curSets.forEach { set ->
            str += "(${set[0] + 1} ${set[1] + 1} ${set[2] + 1})  "
        }
        mCurSetsTv.text = str
    }

    override fun updateShapes() {

    }

    override fun onTimerExpired() {

    }
}
