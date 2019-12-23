package com.cyz.demo.paint.linewave.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;

import com.cyz.demo.paint.linewave.R;
import com.cyz.demo.paint.linewave.model.WaveLine;
import com.cyz.demo.paint.linewave.model.WaveLinePoint;

import java.util.ArrayList;
import java.util.List;

public class WaveLineView extends LineSurfaceView  {

    private static final String TAG = "WaveLineView";

    // 声音的分贝数
    private float mVoiceVolume = 1;
    // 控件的参数
    private int width;
    private int height;
    private int heightCenter;
    // 画布的背景色
    private int mBgColor;
    // 线的集合
    private List<WaveLine> mLines = new ArrayList<>();


    public WaveLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "LineWaveView: attrs");
        initData(context, attrs);
    }

    public void setVoiceVolume(int volume) {
        mVoiceVolume = volume;
    }

    private float getVoiceVolume() {
        return mVoiceVolume / 10 + 1;
    }

    /**
     * 初始化布局
     * @param context：
     * @param attrs：
     */
    private void initData(Context context, AttributeSet attrs) {

        if (context != null && attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveLineView);
            // 从attrs中获取参数值，如果没有自定义设置则设置默认值
            mBgColor = typedArray.getColor(R.styleable.WaveLineView_bgColor, getResources().getColor(R.color.white));
            typedArray.recycle();
        }
    }

    /**
     * 添加一条波浪线
     * @param line：线
     */
    public void addLine(WaveLine line) {
        mLines.add(line);
    }

    /**
     * 画出所有的线
     * @param canvas：获得当前的画布
     * @param passedTime：经过的时间量
     */
    @Override
    public void drawLines(Canvas canvas, long passedTime) {

        // 屏幕参数
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        heightCenter = height / 2;
        // 背景色
        canvas.drawColor(mBgColor);
        // 当前未添加任何线时默认显示的波浪线
        if (mLines == null || mLines.size() < 1 ) {
            addLine(new WaveLine(5f, 5f, 4f, 2f));
        }
        // 画出所有的线
        for (WaveLine line: mLines) {
            drawLine(canvas, line, passedTime);
        }
    }

    /**
     * 画出一条线
     * @param canvas：获得当前的画布
     * @param line：线
     * @param millisPassed：经过的时间量
     */
    private void drawLine(Canvas canvas, WaveLine line, long millisPassed) {

        initLine(line, millisPassed);
        // 连接波浪线所有点
        for (int i = 1; i < line.getmPointSize(); i++) {
            WaveLinePoint lastPoint = line.getmLinePoints().get(i - 1);
            WaveLinePoint nextPoint = line.getmLinePoints().get(i);

            canvas.drawLine(lastPoint.getX(), lastPoint.getY(), nextPoint.getX(), nextPoint.getY(), line.getmLinePaint());
        }
    }

    /**
     * 初始化线的信息
     * @param line：线
     * @param millisPassed：经过的时间量
     */
    private void initLine(WaveLine line, long millisPassed) {
        line.getmLinePoints().clear();

        // 设置波浪线为渐变色（加在构造函数中无效）
        LinearGradient linearGradient = new LinearGradient(0, heightCenter, width, heightCenter, line.getLineColors(), line.getColorPositions(), Shader.TileMode.CLAMP);
        line.getmLinePaint().setShader(linearGradient);

        // 根据时间偏移
        float offset = millisPassed / 1000f * line.getmLineSpeed();
        // 波浪线上每个点（pointSize）之间X轴的间距
        float dx = (float) width / (line.getmPointSize() - 1);

        initPositions(offset, dx, line);
    }

    /**
     * 初始化一条线，计算出线上所有的点坐标
     * @param offset：x轴偏移量
     * @param dx：每个点之间x轴的间距
     * @param line：线
     */
    private void initPositions(float offset, float dx, WaveLine line) {
        int pointSize = line.getmPointSize();

        for (int i = 0; i < pointSize; i++) {
            // 每个点的坐标
            float x = dx * i;
            // 计算振幅参数：收敛函数，范围0~1 ：-（（x-y/2）^2 + (y/2)^2）
            float shakeParam = (float) (-Math.pow(i - pointSize / 2, 2) + Math.pow(pointSize / 2, 2)) / 5000;
            float y = getLineY(x, offset, shakeParam, line);
            line.getmLinePoints().add(new WaveLinePoint(x, y));
        }
    }

    /**
     * 根据参数计算出当前点（pointSize）的Y轴坐标
     * @param x：当前的X轴坐标
     * @param offset：时间偏移量
     * @param shakeParam：振幅参数（收敛）
     * @param line：线
     * @return y：当前X对应的Y值
     */
    private float getLineY(float x, float offset, float shakeParam, WaveLine line) {

        // Math.toRadians(x / 2)：除的数越大波浪峰值的数量越小，Math.pow(getVoiceVolume(), line.getmVoiceSensiable())：声音敏感度越高，声音对振幅的影响越大
        float dy = (float) Math.sin(Math.toRadians(x / 2) + offset + line.getmLineOffset()) * shakeParam * line.getmLineShake() * (float) Math.pow(getVoiceVolume(), line.getmVoiceSensiable());

        // 波浪线上的点在控件高度中间的位置波动
        return heightCenter - dy;
    }

}
