package com.ericwei.sets.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentHomeBinding
import com.ericwei.sets.model.ShapeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var mShapeArray: Array<ShapeView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        mShapeArray = arrayOf(
            binding.s1, binding.s2, binding.s3
        )
        binding.playBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_gameFragment)
        }
        binding.rulesBtn.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_rulesFragment)
        }

        observeViewModel()
        viewModel.updateShapes()
        
        return binding.root
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shapes.collect { shapes ->
                    shapes.forEachIndexed { index, shape ->
                        if (index < mShapeArray.size) {
                            val shapeView = mShapeArray[index]
                            shapeView.mShape = shape
                            shapeView.mDrawFrame = false
                            shapeView.invalidate()
                        }
                    }
                }
            }
        }
    }
}
