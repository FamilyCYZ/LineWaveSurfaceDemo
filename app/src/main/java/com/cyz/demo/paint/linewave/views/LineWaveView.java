package com.cyz.demo.paint.linewave.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;

import com.cyz.demo.paint.linewave.R;
import com.cyz.demo.paint.linewave.presenter.DataUtils;

import java.util.ArrayList;
import java.util.List;

public class LineWaveView extends LineSurfaceView  {

    private static final String TAG = "LineWaveView";

    // 绘图的画笔
    private Paint mLinePaint;
    // 声音的分贝数
    private float mVoiceVolume = 1;
    // 控件的参数
    private int width;
    private int height;
    private int heightCenter;
    // 波浪线上点的集合
    private List<LinePoint> mLinePoints = new ArrayList<>();
    // 波浪线的颜色
    private int[] lineColors = DataUtils.lineColors;
    private float[] colorPositions = DataUtils.colorPositions;

    // 波浪线自定义的参数
    private int mBgColor;       // 画布的背景色
    private int mLineWidth;     // 波浪线的宽度（粗细）
    private float mLineOffset;  // 波浪线偏移的程度
    private float mLineSpeed;   // 波浪线横向移动的速度
    private float mLineShake;   // 波浪线初始化振幅
    private int mPointSize;     // 波浪线上模拟的点数


    public LineWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "LineWaveView: attrs");
        initData(context, attrs);
    }

    /**
     * 初始化布局
     */
    private void initData(Context context, AttributeSet attrs) {

        if (context != null && attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineWaveView);
            // 从attrs中获取参数值，如果没有自定义设置则设置默认值
            mBgColor = typedArray.getColor(R.styleable.LineWaveView_bgColor, getResources().getColor(R.color.white));
            mLineWidth = typedArray.getInt(R.styleable.LineWaveView_lineWidth, 8);
            mLineOffset = typedArray.getFloat(R.styleable.LineWaveView_lineOffset, 10f);
            mLineSpeed = typedArray.getFloat(R.styleable.LineWaveView_lineSpeed, 7f);
            mLineShake = typedArray.getFloat(R.styleable.LineWaveView_lineShake, 3f);
            mPointSize = typedArray.getInt(R.styleable.LineWaveView_pointSize, 200);
            typedArray.recycle();
        }

        initPaint();
    }

    private void initPaint() {
        mLinePaint = new Paint();
        // 线的宽度
        mLinePaint.setStrokeWidth(mLineWidth);
        // 空心
        mLinePaint.setStyle(Paint.Style.STROKE);
        // 抗锯齿
        mLinePaint.setAntiAlias(true);
        // 防抖动
        mLinePaint.setDither(true);
        //设置笔刷样式为原型
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void drawContent(Canvas canvas, long passedTime) {
        initLine(passedTime);
        drawLine(canvas);
    }

    /**
     * 计算出点的位置
     *
     * @param millisPassed：经过的时间量
     */
    private void initLine(long millisPassed) {
        // 屏幕参数
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        heightCenter = height / 2;

        mLinePoints.clear();

        // 设置波浪线为渐变色（加在构造函数中无效）
        LinearGradient linearGradient = new LinearGradient(0, heightCenter, width, heightCenter, lineColors, colorPositions, Shader.TileMode.CLAMP);
        mLinePaint.setShader(linearGradient);

        // 根据时间偏移
        mLineOffset = millisPassed / 1000f * mLineSpeed;
        // 波浪线上每个点（pointSize）之间X轴的间距
        float dx = (float) width / (mPointSize - 1);

        initPositions(mLineOffset, dx, mLineShake, mLinePoints);
    }

    /**
     * 初始化一条线
     *
     * @param offset：x轴偏移量
     * @param dx：每个点之间x轴的间距
     * @param lineShake：震动幅度
     * @param points：所有的点
     */
    private void initPositions(float offset, float dx, float lineShake, List<LinePoint> points) {
        for (int i = 0; i < mPointSize; i++) {
            // 每个点的坐标
            float x = dx * i;
            // 计算振幅参数：收敛函数，范围0~1 ：-（（x-y/2）^2 + (y/2)^2）
            float shakeParam = (float) (-Math.pow(i - mPointSize / 2, 2) + Math.pow(mPointSize / 2, 2)) / 5000;
            float y = getLineY(x, offset, shakeParam, lineShake);
            points.add(new LinePoint(x, y));
        }
    }

    /**
     * 初始化路径
     * @param canvas：获得当前的画布
     */
    private void drawLine(Canvas canvas) {
        canvas.drawColor(mBgColor);

        // 连接波浪线所有点
        for (int i = 1; i < mPointSize; i++) {
            LinePoint lastPoint = mLinePoints.get(i - 1);
            LinePoint nextPoint = mLinePoints.get(i);
            Log.i(TAG, "drawLine: LastPoint = " + lastPoint + ", NextPoint = " + nextPoint);

            canvas.drawLine(lastPoint.x, lastPoint.y, nextPoint.x, nextPoint.y, mLinePaint);

        }
    }

    /**
     * 根据参数计算出当前点（pointSize）的Y轴坐标
     *
     * @param x：当前的X轴坐标
     * @param offset：偏移量
     * @param shakeParam：振幅参数（收敛）
     * @param shakeRatio：振动幅度
     */
    private float getLineY(float x, float offset, float shakeParam, float shakeRatio) {
        // 增加音量对振幅的影响
        float dy = (float) Math.sin(Math.toRadians(x) + offset) * shakeParam * shakeRatio * (float) Math.pow(getVoiceVolume(), 2);

        // 波浪线上的点在控件高度中间的位置波动
        return (float)heightCenter - dy;
    }

    public void setVoiceVolume(int volume) {
        mVoiceVolume = volume;
    }

    private float getVoiceVolume() {
        // 敏感度越高，声音起振点越低
        return mVoiceVolume / 10 + 1;
    }

    /**
     * 波浪线上的点Bean
     */
    class LinePoint {
        float x;
        float y;

        private LinePoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        @NonNull
        public String toString() {
            return "LinePoint{" + "x=" + x + ", y=" + y + '}';
        }
    }

}
