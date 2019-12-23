package com.cyz.demo.paint.linewave.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LineSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private SurfaceHolder mSurfaceHolder;

    // 标志位
    private boolean isDrawing = false;
    // 绘制间隔时间
    private final static int TIME_IN_FRAME = 15;

    public LineSurfaceView(Context context) {
        super(context);
        init();
    }

    public LineSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // surfaceView初始化
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
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

            long passedTime = System.currentTimeMillis() - startTime;

            synchronized (mSurfaceHolder) {
                // 获得当前的Canvas进行绘制（保留旧数据）
                Canvas canvas  = mSurfaceHolder.lockCanvas();
                // 在画布上绘制
                drawLines(canvas, passedTime);
                // 提交画布的内容
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }

            long diffTime = passedTime;
            // 确保每次更新间隔一致
            while(diffTime <= TIME_IN_FRAME) {
                diffTime = System.currentTimeMillis() - startTime;
                // 线程等待
                Thread.yield();
            }
        }
    }

    public void drawLines(Canvas canvas, long passedTime) {

    }

}
