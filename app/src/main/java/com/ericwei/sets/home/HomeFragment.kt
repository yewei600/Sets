package com.ericwei.sets.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ericwei.sets.R
import com.ericwei.sets.databinding.FragmentHomeBinding
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), HomeContract.View, View.OnClickListener {

    private lateinit var mPresenter: HomeContract.Presenter
    private lateinit var mShapeArray: Array<ShapeView>
    private lateinit var mCoroutineScope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        mShapeArray = arrayOf(
            binding.s1, binding.s2, binding.s3
        )
        binding.playBtn.setOnClickListener(this)
        //binding.customBtn.setOnClickListener(this)
        binding.rulesBtn.setOnClickListener(this)

        mCoroutineScope = CoroutineScope(Dispatchers.Main + Job())
        mPresenter = HomePresenter()
        mPresenter.setView(this)
        mCoroutineScope.launch {
            mPresenter.updateShapes()
        }
        return binding.root
    }

    override fun onShapesReceived(shapes: Array<Shape>) {
        shapes.forEachIndexed { index, shape ->
            val shapeView = mShapeArray[index]
            shapeView.mShape = shape
            shapeView.mDrawFrame = false
            shapeView.invalidate()
        }
    }

    override fun onClick(v: View?) {
        val navController = v!!.findNavController()
        when (v!!.id) {
            R.id.playBtn ->
                navController.navigate(R.id.action_homeFragment_to_gameFragment)
            R.id.rulesBtn ->
                navController.navigate(R.id.action_homeFragment_to_rulesFragment)
        }
    }
}
