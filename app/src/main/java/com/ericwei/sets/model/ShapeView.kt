package com.ericwei.sets.model

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class ShapeView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    enum class DrawState {
        WAITING, REDRAW, SELECT, UNSELECT
    }

    lateinit var mShape: ShapeType
    lateinit var mColor: ColorType
    lateinit var mFill: FillType

    private var mDrawState = DrawState.WAITING
    private var mPaint = Paint()
    private var mFramePaint = Paint()
    private var mPath = Path()
    private var mPathMeasure = PathMeasure()
    private val mInset = 5
    private var mFrameWidth: Float = 5f

    init {
        mPathMeasure.setPath(mPath, false)

        mFramePaint.color = Color.LTGRAY
        mFramePaint.isAntiAlias = true
        mFramePaint.isDither = true
        mFramePaint.style = Paint.Style.STROKE
        mFramePaint.strokeJoin = Paint.Join.ROUND
        mFramePaint.strokeCap = Paint.Cap.ROUND
        mFramePaint.strokeWidth = mFrameWidth

        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 7f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)

        if (mDrawState == DrawState.REDRAW) {
            mColor = ColorType.values().random()
            mShape = ShapeType.values().random()
            mFill = FillType.values().random()
        }

        var coords = intArrayOf(0, 0)
        getLocationOnScreen(coords)
        val cX = (width / 2).toFloat()
        val cY = (height / 2).toFloat()
        val radius = (width / 3).toFloat()

        mPath.reset()
        when (mShape) {
            ShapeType.CIRCLE -> getCirclePath(cX, cY, radius, mPath)
            ShapeType.SQUARE -> getRectPath(
                cX - radius,
                cY - radius,
                cX + radius,
                cY + radius,
                mPath
            )
            ShapeType.TRIANGLE -> getTrianglePath((cX - radius), (cY + radius), radius, mPath)
        }

        when (mFill) {
            FillType.FILL -> mPaint.style = Paint.Style.FILL
            FillType.EMPTY -> mPaint.style = Paint.Style.STROKE
            FillType.LINES -> {
                mPaint.style = Paint.Style.STROKE
                drawLinesThrough(mPath)
            }
        }
        mPaint.color = mColor.color

        canvas.drawPath(mPath, mPaint)

        canvas.drawRect(Rect(mInset, mInset, width - mInset, height - mInset), mFramePaint)
    }

    fun animateUserSelection(isAdding: Boolean) {
        //do some animation to show User selected this Shape
        val startVal = mFramePaint.strokeWidth
        val endVal = if (startVal == 5f) 40f else 5f
        ObjectAnimator.ofFloat(this, "frameWidth", startVal, endVal).apply {
            duration = 3000
            interpolator = LinearInterpolator()
            start()
        }
    }

    //attempt 2 just simply change width
    fun redrawShape(state: DrawState) {
        mDrawState = state
        var curWidth = mFramePaint.strokeWidth
        when (state) {
            DrawState.REDRAW ->
                curWidth = 5f
            DrawState.UNSELECT ->
                curWidth = 5f
            DrawState.SELECT ->
                curWidth = 40f
        }
        mFramePaint.strokeWidth = curWidth
        invalidate()
    }

    fun setShapeAttributes(shape: Shape) {
        mShape = shape.shapeType
        mColor = shape.colorType
        mFill = shape.fillType
    }

    private fun drawLinesThrough(path: Path) {
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, false)

        val pointArray = arrayOfNulls<Pair<Float, Float>>(20)
        val length = pathMeasure.getLength()
        var distance = 0f
        val speed = length / 20
        var counter = 0
        val aCoordinates = FloatArray(2)

        while (distance < length && counter < 20) {
            // get point from the path
            pathMeasure.getPosTan(distance, aCoordinates, null)
            pointArray[counter] = Pair(
                aCoordinates[0],
                aCoordinates[1]
            )
            counter++
            distance = distance + speed
        }

        var l = 0
        var r = pointArray.size - 1
        while (l < r) {
            val start = pointArray[l]
            val end = pointArray[r]
            path.moveTo(start!!.first, start.second)
            path.lineTo(end!!.first, end.second)
            l++
            r--
        }
    }

    private fun getCirclePath(x: Float, y: Float, radius: Float, path: Path) {
        path.addCircle(x, y, radius, Path.Direction.CW)
    }

    private fun getRectPath(left: Float, top: Float, right: Float, bottom: Float, path: Path) {
        path.addRect(left, top, right, bottom, Path.Direction.CCW)
    }

    private fun getTrianglePath(x: Float, y: Float, radius: Float, path: Path) {
        val p1 = Point(x.toInt(), y.toInt())  //bottom left point
        val pointX = x + radius
        val pointY = y - radius * 2

        val p2 = Point(pointX.toInt(), pointY.toInt())  //top point
        val p3 = Point((x + radius * 2).toInt(), y.toInt())  //bottom right point

        path.moveTo(p1.x.toFloat(), p1.y.toFloat())
        path.lineTo(p2.x.toFloat(), p2.y.toFloat())
        path.lineTo(p3.x.toFloat(), p3.y.toFloat())
        path.close()
    }

}