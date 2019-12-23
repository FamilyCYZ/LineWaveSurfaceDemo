package com.cyz.demo.paint.linewave.model;

import android.graphics.Paint;

import com.cyz.demo.paint.linewave.presenter.DataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 波浪线
 */
public class WaveLine {

    // 绘线的画笔
    private Paint mLinePaint = new Paint();

    // 波浪线上点的集合
    private List<WaveLinePoint> mLinePoints = new ArrayList<>();

    // 波浪线的颜色
    private int[] lineColors = DataUtils.lineWaveColors;
    private float[] colorPositions = DataUtils.colorPositions;

    // 波浪线自定义的参数
    private int mLineWidth = 8;     // 波浪线的宽度（粗细）
    private float mLineOffset;  // 波浪线偏移的程度
    private float mLineSpeed;   // 波浪线横向移动的速度
    private float mLineShake;   // 波浪线初始化振幅
    private int mPointSize = 200;   // 波浪线上模拟的点数
    private float mVoiceSensiable;    // 对声音的敏感度

    public WaveLine() {
        initPaint();
    }

    public WaveLine(float mLineOffset, float mLineSpeed, float mLineShake, float mVoiceSensiable) {
        this.mLineOffset = mLineOffset;
        this.mLineSpeed = mLineSpeed;
        this.mLineShake = mLineShake;
        this.mVoiceSensiable = mVoiceSensiable;
        initPaint();
    }

    private void initPaint() {
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

    public Paint getmLinePaint() {
        return mLinePaint;
    }

    public List<WaveLinePoint> getmLinePoints() {
        return mLinePoints;
    }

    public void setmLinePaint(Paint mLinePaint) {
        this.mLinePaint = mLinePaint;
    }

    public void setmLinePoints(List<WaveLinePoint> mLinePoints) {
        this.mLinePoints = mLinePoints;
    }

    public int[] getLineColors() {
        return lineColors;
    }

    public void setLineColors(int[] lineColors) {
        this.lineColors = lineColors;
    }

    public float[] getColorPositions() {
        return colorPositions;
    }

    public void setColorPositions(float[] colorPositions) {
        this.colorPositions = colorPositions;
    }

    public int getmLineWidth() {
        return mLineWidth;
    }

    public void setmLineWidth(int mLineWidth) {
        this.mLineWidth = mLineWidth;
    }

    public float getmLineOffset() {
        return mLineOffset;
    }

    public void setmLineOffset(float mLineOffset) {
        this.mLineOffset = mLineOffset;
    }

    public float getmLineSpeed() {
        return mLineSpeed;
    }

    public void setmLineSpeed(float mLineSpeed) {
        this.mLineSpeed = mLineSpeed;
    }

    public float getmLineShake() {
        return mLineShake;
    }

    public void setmLineShake(float mLineShake) {
        this.mLineShake = mLineShake;
    }

    public int getmPointSize() {
        return mPointSize;
    }

    public void setmPointSize(int mPointSize) {
        this.mPointSize = mPointSize;
    }

    public float getmVoiceSensiable() {
        return mVoiceSensiable;
    }

    public void setmVoiceSensiable(int mVoiceSensiable) {
        this.mVoiceSensiable = mVoiceSensiable;
    }

}
