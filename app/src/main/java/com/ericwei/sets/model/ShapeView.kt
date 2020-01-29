package com.ericwei.sets.model

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ShapeView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var mShape: Shape? = null
    var mDrawFrame: Boolean = true
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

        mShape?.let {
            var coords = intArrayOf(0, 0)
            getLocationOnScreen(coords)
            val cX = (width / 2).toFloat()
            val cY = (height / 2).toFloat()
            val radius = (width / 3).toFloat()

            mPath.reset()
            when (mShape?.shapeType) {
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

            when (mShape?.fillType) {
                FillType.FILL -> mPaint.style = Paint.Style.FILL
                FillType.EMPTY -> mPaint.style = Paint.Style.STROKE
                FillType.LINES -> {
                    mPaint.style = Paint.Style.STROKE
                    drawLinesThrough(mPath)
                }
            }
            var curWidth = 5f
            when (mShape?.drawState) {
                DrawState.REDRAW ->
                    curWidth = 5f
                DrawState.UNSELECT ->
                    curWidth = 5f
                DrawState.SELECT ->
                    curWidth = 40f
            }
            mFramePaint.strokeWidth = curWidth

            mPaint.color = mShape?.colorType!!.color
            canvas.drawPath(mPath, mPaint)

            if (mDrawFrame) {
                canvas.drawRect(Rect(mInset, mInset, width - mInset, height - mInset), mFramePaint)
            }
        }
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