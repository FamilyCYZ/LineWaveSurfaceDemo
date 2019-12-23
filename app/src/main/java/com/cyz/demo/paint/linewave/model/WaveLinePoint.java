package com.cyz.demo.paint.linewave.model;

import android.support.annotation.NonNull;

/**
 * 波浪线上的点
 */
public class WaveLinePoint {
    private float x;
    private float y;

    public WaveLinePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    @NonNull
    public String toString() {
        return "WaveLinePoint{" + "x=" + x + ", y=" + y + '}';
    }

}
