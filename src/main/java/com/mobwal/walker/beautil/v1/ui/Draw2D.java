package com.mobwal.walker.beautil.v1.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobwal.walker.beautil.v1.wBeacon;

public class Draw2D extends View {
    // масштаб
    private float mStepMeter = 100;
    // маячки
    private wBeacon[] mBeacons;
    // текущее местоположение
    private float[] mIm = new float[] {};

    public float mAzimuthInDegrees = 0;

    public void setStepMeter(float value) {
        mStepMeter = value;
    }

    public float getStepMeter() {
        return mStepMeter;
    }

    public void setBeacons(wBeacon[] value) {
        mBeacons = value;
    }

    public wBeacon[] getBeacons() {
        return mBeacons;
    }

    public void setIm(float[] value) {
        mIm = value;
    }

    public float[] getIm() {
        return mIm;
    }

    public void setAzimuthInDegrees(float value) {
        mAzimuthInDegrees = value;
    }

    public float getAzimuthInDegrees() {
        return mAzimuthInDegrees;
    }

    public Draw2D(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mBeacons = new wBeacon[0];
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // отрисовка решётки
        drawGrid(canvas, getAzimuthInDegrees());

        if(getIm().length > 0) {
            drawCurrentPosition(canvas, getIm()[0], getIm()[1]);
        }

        // отрисовка beacon
        for (wBeacon beacon:
                getBeacons()) {
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.FILL);

            canvas.drawCircle(beacon.x.floatValue(), beacon.y.floatValue(), 10, paint);
        }

        //canvas.rotate(0, canvas.getWidth()/2 , canvas.getHeight()/2);

        // маркировка beacon
        for (wBeacon beacon:
                getBeacons()) {
            Paint paint = new Paint();

            paint.setColor(Color.BLACK);
            paint.setTextSize(40.0f);
            if(beacon.id2 != null && beacon.id3 != null) {
                canvas.drawText(beacon.id2.toString() + ";" + beacon.id3.toString(), beacon.x.floatValue(), beacon.y.floatValue(), paint);
            }
        }
    }

    /**
     * Сетка
     *
     * @param canvas
     * @param azimuthInDegrees
     */
    private void drawGrid(@NonNull Canvas canvas, float azimuthInDegrees) {
        float w = getWidth();
        float h = getHeight();

        //canvas.rotate(180, canvas.getWidth()/2 , canvas.getHeight()/2);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1);

        for(float i = mStepMeter - h; i < (h * 2); i += mStepMeter) {
            canvas.drawLine(-h, i, w * 2, i, paint);
        }

        for (float i = mStepMeter - w; i < (w * 2); i += mStepMeter) {
            canvas.drawLine(i, -w, i, h * 2, paint);
        }
    }

    /**
     * Отрисока текущего местоположения
     *
     * @param canvas
     * @param x
     * @param y
     */
    private void drawCurrentPosition(@NonNull Canvas canvas, float x, float y) {
        Paint paint = new Paint();
        paint.setColor(Color.RED); // установим зелёный цвет
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x, y, 15, paint);
    }

}
