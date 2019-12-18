package com.cyz.demo.paint.linewave;

import android.animation.ValueAnimator;
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
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LineWaveView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "LineWaveView";

    private SurfaceHolder mSurfaceHolder;
    private ExecutorService mExecutorService;

    // 绘图的画布
    private Canvas mLineCanvas;
    // 绘图的画笔
    private Paint mLinePaint;
    // 绘图的路径
    private Path mLinePath;

    private int[] mLineColors = {0xff728eff, 0xff6fd3ea, 0xff5996ff, 0xff3a68e6, 0xff5fa4ff, 0xff559df7};  // 波浪线的颜色（渐变）
    private float[] colorPositions = {0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f};

    public LineWaveView(Context context) {
        super(context);
        initData();
    }

    public LineWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    /**
     * 初始化布局
     */
    private void initData() {

        mExecutorService = Executors.newCachedThreadPool();
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mLineCanvas = new Canvas();
        mLinePath = new Path();

        mLinePaint = new Paint();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                drawLine();
            }
        });
    }

    private void drawLine() {
        Log.i(TAG, "drawLine: 走了这吗？");

        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(8);

        // 设置波浪线为渐变色（放在initData中只显示最后一个颜色）
        LinearGradient linearGradient = new LinearGradient(0, getMeasuredHeight(), getMeasuredWidth(), 0, mLineColors, colorPositions, Shader.TileMode.CLAMP);
        mLinePaint.setShader(linearGradient);

        try {
            mLineCanvas = mSurfaceHolder.lockCanvas();
            mLineCanvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mLinePaint);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mLineCanvas) {
                mSurfaceHolder.unlockCanvasAndPost(mLineCanvas);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
