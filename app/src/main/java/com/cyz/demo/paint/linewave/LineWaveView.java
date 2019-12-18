package com.cyz.demo.paint.linewave;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LineWaveView extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    private static final String TAG = "LineWaveView";

    private SurfaceHolder mSurfaceHolder;

    // 绘图的画布
    private Canvas mLineCanvas;
    // 绘图的画笔
    private Paint mLinePaint;
    // 绘图的路径
    private Path mLinePath;
    // 声音的分贝数
    private float mVoiceVolume = 1;
    // 波浪线上点的集合
    private List<LinePoint> mLinePoints = new ArrayList<>();
    // 波浪线的颜色
    private int[] lineColors;
    // 标志位
    private boolean isDrawing = false;
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


    /**
     * 波浪线上的点Bean
     */
    class LinePoint {
        float x;
        float y;

        public LinePoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "LinePoint{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public LineWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        isDrawing = true;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.lineWave);
        // 从attrs中获取参数值，如果没有自定义设置则设置默认值
        mBgColor = typedArray.getColor(R.styleable.lineWave_bgColor, getResources().getColor(R.color.white));
        mLineWidth = typedArray.getInt(R.styleable.lineWave_lineWidth, 8);
        mLineOffset = typedArray.getFloat(R.styleable.lineWave_lineOffset, 10f);
        mLineSpeed = typedArray.getFloat(R.styleable.lineWave_lineSpeed, 2f);
        mLineShake = typedArray.getFloat(R.styleable.lineWave_lineShake, 0.2f);
        mPointSize = typedArray.getInt(R.styleable.lineWave_pointSize, 200);
        mSensitivity = typedArray.getInt(R.styleable.lineWave_sensitivity, 5);

        initData();
    }

    /**
     * 初始化布局
     */
    private void initData() {

        // surfaceView初始化
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        // 屏幕参数
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        // 波浪线的颜色
        lineColors = DataUtils.lineColors;
        // 绘画初始化
        mLineCanvas = new Canvas();
        mLineCanvas.drawColor(mBgColor);

        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setAntiAlias(true);

        mLinePath = new Path();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (isDrawing && mSurfaceHolder != null) {
            initLine(startTime, System.currentTimeMillis());
        }
    }

    /**
     * 计算出点的位置
     *
     * @param startTime
     * @param nowTime
     */
    private void initLine(long startTime, long nowTime) {
        mLinePoints.clear();
        // 根据时间偏移
        long millsPassed = System.currentTimeMillis() - startTime;
        mLineOffset = millsPassed / 1000f * mLineSpeed;
        // 波浪线上每个点（pointSize）之间X轴的间距
        float dx = (float) width / (mPointSize - 1);

        initPositions(mLineOffset, dx, mLineShake, mLinePoints);
        initDraw();
    }

    /**
     * 初始化一条线
     *
     * @param offset
     * @param dx
     * @param shakeRatio
     * @param points
     */
    private void initPositions(float offset, float dx, float shakeRatio, List<LinePoint> points) {
        for (int i = 0; i < mPointSize; i++) {
            // 每个点的x坐标
            float x = dx * i;
            // 计算振幅参数：收敛函数 （i-mPointSize/2）^2 + (mPointSize/2)^2
            float shakeParam = (float) (-Math.pow((i - mPointSize / 2), 2) + Math.pow(mPointSize / 2, 2));
            // 获得Y轴坐标
            float y = getLineY(x, offset, shakeParam, shakeRatio);
            // 存储计算出的每个点
            points.add(new LinePoint(x, y));
        }
    }

    /**
     * 根据参数计算出当前点（pointSize）的Y轴坐标
     *
     * @param x：当前的X轴坐标
     * @param offset：偏移量
     * @param shakeParam：振幅参数（收敛）
     * @param shakeRatio
     */
    private float getLineY(float x, float offset, float shakeParam, float shakeRatio) {
        float dy = (float) Math.sin(Math.toRadians(x) + offset) * shakeParam * shakeRatio * getVoiceVolume();
        // 波浪线上的点在控件高度中间的位置波动
        return height / 2 - dy;
    }

    public void setVoiceVolume(int volume) {
        mVoiceVolume = volume;
    }

    private float getVoiceVolume() {
        // 敏感度越高，声音起振点越低
        return (mVoiceVolume + mSensitivity) / 10 + 1;
    }

    /**
     * 画出波浪线
     */
    private void initDraw() {

        try {
            // 获得当前的Canvas绘图对象（之前的绘图操作如需清除需要使用drawColor()方法）
            mLineCanvas = mSurfaceHolder.lockCanvas();
            drawLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mLineCanvas != null) {
                // 对画布内容进行提交
                mSurfaceHolder.unlockCanvasAndPost(mLineCanvas);
            }
        }
    }

    /**
     * 初始化路径
     */
    private void drawLine() {
        // 从上一个点移动到下一个点
        for (int i = 1; i < mPointSize; i++) {
            LinePoint lastPoint = mLinePoints.get(i - 1);
            LinePoint nextPoint = mLinePoints.get(i);
            Log.i(TAG, "drawLine: LastPoint = " + lastPoint + ", NextPoint = " + nextPoint);

            int colorIndexMax = lineColors.length - 1;
            // 将颜色分为等距离的几段，取该段位置的颜色，（%colorIndexMax）放在后面就没有渐变的效果了
            int lastColor = lineColors[i % colorIndexMax / (mPointSize / colorIndexMax)];
            int nextColor = lineColors[i % colorIndexMax / (mPointSize / colorIndexMax) + 1];
            Log.i(TAG, "drawLine: LastColor = " + lastColor + ", NextColor = " + nextColor);
            // 设置波浪线为渐变色
            LinearGradient linearGradient = new LinearGradient(0, height / 2, width, height / 2, lastColor, nextColor, Shader.TileMode.CLAMP);
            mLinePaint.setShader(linearGradient);

            mLineCanvas.drawLine(lastPoint.x, lastPoint.y, nextPoint.x, nextPoint.y, mLinePaint);
            mLineCanvas.drawLine(lastPoint.x, lastPoint.y, nextPoint.x, nextPoint.y, mLinePaint);

        }
    }

}
