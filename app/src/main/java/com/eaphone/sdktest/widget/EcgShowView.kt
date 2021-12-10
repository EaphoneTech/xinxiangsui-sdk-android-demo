package com.eaphone.sdktest.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * 实时心电波形图
 * hlh
 */

open class EcgShowView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var SHOW_MODEL = 0
    val SHOW_MODEL_ALL = 0x00
    val SHOW_MODEL_DYNAMIC_SCROLL = 0x01
    val SHOW_MODEL_DYNAMIC_REFRESH = 0x02

    private var mWidth: Float = 0.toFloat()
    private var mHeight: Float = 0.toFloat()
    private var paint: Paint? = null
    private var path: Path? = null
    private var dataList: ArrayList<Int>? = null

    private var scrollIndex = 0
    private var timer: Timer ?= null
    private var timerTask: TimerTask ?= null

    private var refreshList: MutableList<Int>? = null
    private var showIndex: Int = 0

    private val MAX_VALUE = 30000
    private var intervalNumHeart= 1
    private var intervalRowHeart = 0.0f
    private var intervalColumnHeart = 0.0f

    private val HEART_LINE_STROKE_WIDTH = 1.0f
    private var mHeartLinestrokeWidth = 0.0f

    private val GRID_LINE_WIDTH = 0.5f
    private var mGridLineWidth = 0.0f


    private var maxRow: Int = 90

    private var row: Int = 0
    private var intervalRow = 0.0f
    private var column: Int = 0
    private var intervalColumn = 0.0f
    private var mGridstrokeWidthAndHeight = 0.0f
    //背景颜色
    private var mBackgroundColor = Color.TRANSPARENT
    //折现的颜色
    private var mLineColor = Color.parseColor("#76f112")
    //网格颜色
    protected var mGridColor = Color.parseColor("#1b4200")
    //小网格颜色
    protected var mSGridColor = Color.parseColor("#092100")

    init {
        init()
    }

    private fun init() {
        paint = Paint()
        path = Path()
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mWidth = measuredWidth.toFloat()
        mHeight = measuredHeight.toFloat()
        mGridLineWidth = ConvertUtils.dp2px(GRID_LINE_WIDTH).toFloat()
        mHeartLinestrokeWidth = ConvertUtils.dp2px( HEART_LINE_STROKE_WIDTH).toFloat()
        mGridstrokeWidthAndHeight = mWidth/15
        column = (mWidth / mGridstrokeWidthAndHeight).toInt()
        intervalColumn = mWidth / column
        row = (mHeight / mGridstrokeWidthAndHeight).toInt()
        intervalRow = intervalColumn
        initData()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBackgroundColor)
        //drawGrid(canvas)
        when (SHOW_MODEL) {
            SHOW_MODEL_ALL -> drawHeartAll(canvas)
            SHOW_MODEL_DYNAMIC_SCROLL -> drawHeartScroll(canvas)
            SHOW_MODEL_DYNAMIC_REFRESH -> drawHeartRefresh(canvas)
        }
    }

    fun showLine(point: Int) {
        if(SHOW_MODEL == SHOW_MODEL_DYNAMIC_REFRESH){
            if (refreshList == null) {
                refreshList = ArrayList()
            }
            refreshList?.add(point)
            dataList?.add(point)
        } else{
            scrollIndex++
            dataList?.add(point)
        }
        postInvalidate()
    }

    fun reset(){
        refreshList= arrayListOf()
        dataList = arrayListOf()
        scrollIndex = 0
        postInvalidate()
    }

    private fun drawHeartRefresh(canvas: Canvas) {
        paint!!.reset()
        path!!.reset()
        paint!!.style = Paint.Style.STROKE
        paint!!.color = mLineColor
        paint!!.strokeWidth = mHeartLinestrokeWidth
        paint!!.isAntiAlias = true
        path!!.moveTo(0f, mHeight / 2)

        val nowIndex = if (refreshList == null) 0 else refreshList!!.size
        if (nowIndex == 0) {
            return
        }
        showIndex = if (nowIndex < intervalNumHeart) {
            nowIndex - 1
        } else {
            (nowIndex - 1) % intervalNumHeart
        }
        for (i in 0 until intervalNumHeart) {
            if (i > refreshList!!.size - 1) {
                break
            }
            if (dataList == null || dataList!!.isEmpty() || refreshList == null || refreshList!!.isEmpty()) {
                break
            }
            if (nowIndex <= intervalNumHeart) {
                this.dataList!![i] = refreshList!![i]
            } else {
                val times = (nowIndex - 1) / intervalNumHeart
                val temp = times * intervalNumHeart + i
                if (temp < nowIndex) {
                    this.dataList!![i] = refreshList!![temp]
                }
            }
        }
        var nowX: Float
        var nowY: Float
        for (i in dataList!!.indices) {
            nowX = i * intervalRowHeart
            var dataValue = dataList!![i]
            if (dataValue > 0) {
                if (dataValue > MAX_VALUE) {
                    dataValue = MAX_VALUE
                }
            } else {
                if (dataValue < -MAX_VALUE) {
                    dataValue = -(MAX_VALUE)
                }
            }
            nowY = mHeight / 2 - dataValue * intervalColumnHeart
            if (i - 1 == showIndex) {
                path!!.moveTo(nowX, nowY)
            } else {
                path!!.lineTo(nowX, nowY)
            }
        }
        canvas.drawPath(path!!, paint!!)
    }

    private fun drawHeartScroll(canvas: Canvas) {
        if (dataList == null || dataList!!.isEmpty()) {
            return
        }
        paint?.reset()
        path?.reset()
        paint?.style = Paint.Style.STROKE
        paint?.color = mLineColor
        paint?.strokeWidth = mHeartLinestrokeWidth
        paint?.isAntiAlias = true
        path?.moveTo(0f, mHeight / 2)

        val scrollEndIndex = scrollIndex
        var scrollStartIndex = scrollEndIndex - intervalNumHeart
        if (scrollStartIndex < 0) {
            scrollStartIndex = 0
        }
        var nowX: Float
        var nowY: Float
        for (i in scrollStartIndex until scrollEndIndex) {
            nowX = (i - scrollStartIndex) * intervalRowHeart
            if(dataList!= null && dataList!!.size >=i){
                var dataValue = dataList!![i]
                if (dataValue > 0) {
                    if (dataValue > MAX_VALUE) {
                        dataValue = MAX_VALUE
                    }
                } else {
                    if (dataValue < -MAX_VALUE) {
                        dataValue = -(MAX_VALUE)
                    }
                }
                nowY = mHeight / 2 - dataValue * intervalColumnHeart
                path?.lineTo(nowX, nowY)
            }

        }

        canvas.drawPath(path!!, paint!!)
    }

    private fun drawHeartAll(canvas: Canvas) {
        if (dataList == null || dataList!!.isEmpty()) {
            return
        }
        paint!!.reset()
        path!!.reset()
        paint!!.style = Paint.Style.STROKE
        paint!!.color = mLineColor
        paint!!.strokeWidth = mHeartLinestrokeWidth
        paint!!.isAntiAlias = true
        path!!.moveTo(0f, mHeight / 2)
        var nowX: Float
        var nowY: Float
        for (i in dataList!!.indices) {
            nowX = i * intervalRowHeart
            var dataValue = dataList!![i]
            if (dataValue > 0) {
                if (dataValue > MAX_VALUE) {
                    dataValue = MAX_VALUE
                }
            } else {
                if (dataValue < -MAX_VALUE) {
                    dataValue = -(MAX_VALUE)
                }
            }
            nowY = mHeight / 2 - dataValue * intervalColumnHeart
            path!!.lineTo(nowX, nowY)
        }
        canvas.drawPath(path!!, paint!!)
    }

    fun setLinColor(color:Int){
        mLineColor = color
    }

    fun setData(data: ArrayList<Int>?, model: Int, maxRow:Int) {
        this.dataList = data
        this.SHOW_MODEL = model
        this.maxRow = maxRow
        initData()
    }

    private fun initData() {
        if(dataList==null){
            return
        }
        when (SHOW_MODEL) {
            SHOW_MODEL_ALL -> {
                intervalNumHeart = dataList!!.size
                intervalRowHeart = mWidth / intervalNumHeart
                intervalColumnHeart = mHeight / (MAX_VALUE * 2)
            }
            SHOW_MODEL_DYNAMIC_SCROLL -> {
                intervalRowHeart =  mWidth / maxRow
                intervalNumHeart = (mWidth / intervalRowHeart).toInt()
                intervalColumnHeart = mHeight / (MAX_VALUE * 2)
            }
            SHOW_MODEL_DYNAMIC_REFRESH -> {
                intervalRowHeart =  mWidth / maxRow
                intervalNumHeart = (mWidth / intervalRowHeart).toInt()
                intervalColumnHeart = mHeight / (MAX_VALUE * 2)
            }
        }
    }

    private fun startScrollTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                if (scrollIndex < dataList!!.size) {
                    scrollIndex++
                }
            }
        }
        timer?.schedule(timerTask, 0, 33)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        when (SHOW_MODEL) {
            SHOW_MODEL_DYNAMIC_SCROLL -> {
                timer?.cancel()
            }
        }
    }

}
