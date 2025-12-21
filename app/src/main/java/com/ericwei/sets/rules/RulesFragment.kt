package com.ericwei.sets.rules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ericwei.sets.R
import com.ericwei.sets.model.ShapeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RulesFragment : Fragment() {

    private val viewModel: RulesViewModel by viewModels()
    private lateinit var mValidShapeArray: Array<ShapeView>
    private lateinit var mInvalidShapeArray: Array<ShapeView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rules, container, false)
        mValidShapeArray = arrayOf(
            view.findViewById(R.id.vs1), view.findViewById(R.id.vs2), view.findViewById(R.id.vs3),
            view.findViewById(R.id.vs4), view.findViewById(R.id.vs5), view.findViewById(R.id.vs6),
            view.findViewById(R.id.vs7), view.findViewById(R.id.vs8), view.findViewById(R.id.vs9)
        )

        mInvalidShapeArray = arrayOf(
            view.findViewById(R.id.ivs1), view.findViewById(R.id.ivs2), view.findViewById(R.id.ivs3),
            view.findViewById(R.id.ivs4), view.findViewById(R.id.ivs5), view.findViewById(R.id.ivs6),
            view.findViewById(R.id.ivs7), view.findViewById(R.id.ivs8), view.findViewById(R.id.ivs9)
        )

        observeViewModel()
        viewModel.getShapesForRulesPage()
        
        return view
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.validShapes.forEachIndexed { index, shape ->
                        if (index < mValidShapeArray.size) {
                            mValidShapeArray[index].apply {
                                mShape = shape
                                mDrawFrame = false
                                invalidate()
                            }
                        }
                    }
                    state.invalidShapes.forEachIndexed { index, shape ->
                        if (index < mInvalidShapeArray.size) {
                            mInvalidShapeArray[index].apply {
                                mShape = shape
                                mDrawFrame = false
                                invalidate()
                            }
                        }
                    }
                }
            }
        }
    }
}
