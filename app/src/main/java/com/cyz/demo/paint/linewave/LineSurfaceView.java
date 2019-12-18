package com.cyz.demo.paint.linewave;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LineSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private SurfaceHolder mSurfaceHolder;
    Canvas canvas;
    // 标志位
    private boolean isDrawing = false;

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

            try {
                // 获得当前的Canvas（）
                canvas = mSurfaceHolder.lockCanvas();
                drawContent(canvas, System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != canvas) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void drawContent(Canvas canvas, long passedTime) {

    }

}
