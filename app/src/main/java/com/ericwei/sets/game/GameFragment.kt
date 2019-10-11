package com.ericwei.sets.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentGameBinding
import com.ericwei.sets.model.Shape

class GameFragment : Fragment(), GameContract.View {

    private lateinit var mGamePresenter: GameContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentGameBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )

        mGamePresenter = GamePresenter(this)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mGamePresenter.startTimer()
        mGamePresenter.getGameShapes()
    }

    override fun onGameShapesReceived(gameArray: ArrayList<Shape>) {

    }

    override fun updateShapes() {

    }

    override fun onTimerExpired() {

    }
}
