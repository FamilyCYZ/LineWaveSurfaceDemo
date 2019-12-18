package com.cyz.demo.paint.linewave;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LineWaveView extends LineSurfaceView  {

    private static final String TAG = "LineWaveView";

    // 绘图的画笔
    private Paint mLinePaint = new Paint();
    // 声音的分贝数
    private float mVoiceVolume = 1;
    // 波浪线上点的集合
    private List<LinePoint> mLinePoints = new ArrayList<>();
    // 波浪线的颜色
    private int[] lineColors;
    private int width;
    private int height;

    // 波浪线自定义的参数
    private int mBgColor;       // 画布的背景色
    private int mLineWidth;     // 波浪线的宽度（粗细）
    private float mLineOffset;  // 波浪线偏移的程度
    private float mLineSpeed;   // 波浪线横向移动的速度
    private float mLineShake;   // 波浪线初始化振幅
    private int mPointSize;     // 波浪线上模拟的点数
    private int mSensitivity;   // 震动敏感度


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
            mLineSpeed = typedArray.getFloat(R.styleable.LineWaveView_lineSpeed, 4f);
            mLineShake = typedArray.getFloat(R.styleable.LineWaveView_lineShake, 3f);
            mPointSize = typedArray.getInt(R.styleable.LineWaveView_pointSize, 200);
            mSensitivity = typedArray.getInt(R.styleable.LineWaveView_sensitivity, 5);
            typedArray.recycle();
        }

        // 波浪线的颜色
        lineColors = DataUtils.lineColors;

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
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

        mLinePoints.clear();
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
     * @param shakeRatio：震动幅度
     * @param points：所有的点
     */
    private void initPositions(float offset, float dx, float shakeRatio, List<LinePoint> points) {
        for (int i = 0; i < mPointSize; i++) {
            // 每个点的x坐标
            float x = dx * i;
            // 计算振幅参数：收敛函数，范围0~1 ：-（（2x-y）/y）^2 + 1
            float shakeParam =  (float)-Math.pow(((2 * i - mPointSize) / mPointSize), 2) + 1;
            // 获得Y轴坐标
            float y = getLineY(x, offset, shakeParam, shakeRatio);
            // 存储计算出的每个点
            points.add(new LinePoint(x, y));
        }
    }

    /**
     * 初始化路径
     */
    private void drawLine(Canvas canvas) {
        canvas.drawColor(mBgColor);
        int colorIndexMax = lineColors.length - 1;

        // 从上一个点移动到下一个点
        for (int i = 1; i < mPointSize; i++) {
            LinePoint lastPoint = mLinePoints.get(i - 1);
            LinePoint nextPoint = mLinePoints.get(i);
            Log.i(TAG, "drawLine: LastPoint = " + lastPoint + ", NextPoint = " + nextPoint);

            // 将颜色分为等距离的几段，取该段位置的颜色，（%colorIndexMax）放在后面就没有渐变的效果了
            int lastColor = lineColors[i % colorIndexMax / (mPointSize / colorIndexMax)];
            int nextColor = lineColors[i % colorIndexMax / (mPointSize / colorIndexMax) + 1];
            Log.i(TAG, "drawLine: LastColor = " + lastColor + ", NextColor = " + nextColor);
            // 设置波浪线为渐变色
            LinearGradient linearGradient = new LinearGradient(0, height / 2, width, height / 2, lastColor, nextColor, Shader.TileMode.CLAMP);
            mLinePaint.setShader(linearGradient);

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
        // 防止高度溢出

        // 波浪线上的点在控件高度中间的位置波动
        return (float)height / 2 - dy;
    }

    public void setVoiceVolume(int volume) {
        mVoiceVolume = volume;
    }

    private float getVoiceVolume() {
        // 敏感度越高，声音起振点越低
        return (mVoiceVolume + mSensitivity) / 10 + 1;
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
        public String toString() {
            return "LinePoint{" + "x=" + x + ", y=" + y + '}';
        }
    }

}
