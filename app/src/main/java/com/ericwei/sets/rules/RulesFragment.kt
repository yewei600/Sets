package com.ericwei.sets.rules


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ericwei.sets.R
import com.ericwei.sets.model.Shape
import com.ericwei.sets.model.ShapeView

class RulesFragment : Fragment(), RulesContract.View {

    private lateinit var mPresenter: RulesPresenter
    private lateinit var mValidShapeArray: Array<ShapeView>
    private lateinit var mInvalidShapeArray: Array<ShapeView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rules, container, false)
        mValidShapeArray = arrayOf(
            view.findViewById(R.id.vs1),
            view.findViewById(R.id.vs2),
            view.findViewById(R.id.vs3),
            view.findViewById(R.id.vs4),
            view.findViewById(R.id.vs5),
            view.findViewById(R.id.vs6),
            view.findViewById(R.id.vs7),
            view.findViewById(R.id.vs8),
            view.findViewById(R.id.vs9)
        )

        mInvalidShapeArray = arrayOf(
            view.findViewById(R.id.ivs1),
            view.findViewById(R.id.ivs2),
            view.findViewById(R.id.ivs3),
            view.findViewById(R.id.ivs4),
            view.findViewById(R.id.ivs5),
            view.findViewById(R.id.ivs6),
            view.findViewById(R.id.ivs7),
            view.findViewById(R.id.ivs8),
            view.findViewById(R.id.ivs9)
        )

        mPresenter = RulesPresenter(this)
        mPresenter.getShapesForRulesPage()
        return view
    }

    override fun onRulesShapesReceived(shapes: Array<Shape>, forValidArray: Boolean) {
        if (forValidArray) {
            shapes.forEachIndexed { index, shape ->
                val validShape = mValidShapeArray[index]
                validShape.mShape = shape
                validShape.mDrawFrame = false
                validShape.invalidate()
            }
        } else {
            shapes.forEachIndexed { index, shape ->
                val invalidShape = mInvalidShapeArray[index]
                invalidShape.mShape = shape
                invalidShape.mDrawFrame = false
                invalidShape.invalidate()
            }
        }
    }
}
