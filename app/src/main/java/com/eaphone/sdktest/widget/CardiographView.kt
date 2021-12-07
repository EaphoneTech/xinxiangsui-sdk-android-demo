package com.eaphone.sdktest.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * 网格背景
 * hlh
 */
class CardiographView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    //画笔
    private var mPaint: Paint?= null
    //折现的颜色
    private var mLineColor = Color.parseColor("#76f112")
    //网格颜色
    private var mGridColor = Color.parseColor("#1b4200")
    //小网格颜色
    private var mSGridColor = Color.parseColor("#092100")
    //背景颜色
    private var mBackgroundColor = Color.BLACK
    //自身的大小
    private var mWidth: Int = 0
    private var mHeight:Int = 0

    //网格宽度
    private var mGridWidth = 100
    //小网格的宽度
    private var mSGridWidth = 20

    //心电图折现
    private var mPath: Path?= null

    init {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPath = Path()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w
        mHeight = h
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        initBackground(canvas)
    }


    //绘制背景
    private fun initBackground(canvas: Canvas) {
        canvas.drawColor(mBackgroundColor)
        mGridWidth = mWidth / 15
        mSGridWidth = mGridWidth / 5

        //-------画小网格-----
        //竖线个数
        val vSNum = mWidth / mSGridWidth

        //横线个数
        val hSNum = mHeight / mSGridWidth
        mPaint?.color = mSGridColor
        mPaint?.strokeWidth = 1f
        //画竖线
        val s = mGridWidth/5f
        for (i in 0 until vSNum + 1) {
            canvas.drawLine(i * s, 0f, i * s, mHeight.toFloat(), mPaint!!)
        }
        //画横线
        for (i in 0 until hSNum + 1) {
            canvas.drawLine(0f, i * s, mWidth.toFloat(), i * s, mPaint!!)
        }
        //-------画大网格-----
        //竖线个数
        val vNum = mWidth / mGridWidth
        //横线个数
        val hNum = mHeight / mGridWidth
        mPaint?.color = mGridColor
        mPaint?.strokeWidth = 1.2f
        //画竖线
        for (i in 0 until vNum + 1) {
            canvas.drawLine((i * mGridWidth).toFloat(), 0f, (i * mGridWidth).toFloat(), mHeight.toFloat(), mPaint!!)
        }
        //画横线
        for (i in 0 until hNum + 1) {
            canvas.drawLine(0f, (i * mGridWidth).toFloat(), mWidth.toFloat(), (i * mGridWidth).toFloat(), mPaint!!)
        }
    }

}