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

        mFramePaint.apply {
            color = Color.LTGRAY
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mFrameWidth
        }

        mPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 7f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mShape?.let { shape ->
            val cX = (width / 2).toFloat()
            val cY = (height / 2).toFloat()
            val radius = (width / 3).toFloat()

            mPath.reset()
            when (shape.shapeType) {
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

            when (shape.fillType) {
                FillType.FILL -> mPaint.style = Paint.Style.FILL
                FillType.EMPTY -> mPaint.style = Paint.Style.STROKE
                FillType.LINES -> {
                    mPaint.style = Paint.Style.STROKE
                    drawLinesThrough(mPath)
                }
            }
            
            mFramePaint.strokeWidth = when (shape.drawState) {
                DrawState.REDRAW -> 5f
                DrawState.UNSELECT -> 5f
                DrawState.SELECT -> 40f
            }

            mPaint.color = shape.colorType.color
            canvas.drawPath(mPath, mPaint)

            if (mDrawFrame) {
                canvas.drawRect(
                    Rect(mInset, mInset, width - mInset, height - mInset),
                    mFramePaint
                )
            }
        }
    }

    private fun drawLinesThrough(path: Path) {
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, false)

        val pointArray = arrayOfNulls<Pair<Float, Float>>(20)
        val length = pathMeasure.getLength()
        val speed = length / 20
        var distance = 0f
        var counter = 0
        val aCoordinates = FloatArray(2)

        while (distance < length && counter < 20) {
            pathMeasure.getPosTan(distance, aCoordinates, null)
            pointArray[counter] = Pair(aCoordinates[0], aCoordinates[1])
            counter++
            distance += speed
        }

        var l = 0
        var r = pointArray.size - 1
        while (l < r) {
            val start = pointArray[l]
            val end = pointArray[r]
            if (start != null && end != null) {
                path.moveTo(start.first, start.second)
                path.lineTo(end.first, end.second)
            }
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
        val p1 = Point(x.toInt(), y.toInt())
        val p2 = Point((x + radius).toInt(), (y - radius * 2).toInt())
        val p3 = Point((x + radius * 2).toInt(), y.toInt())

        path.moveTo(p1.x.toFloat(), p1.y.toFloat())
        path.lineTo(p2.x.toFloat(), p2.y.toFloat())
        path.lineTo(p3.x.toFloat(), p3.y.toFloat())
        path.close()
    }
}
