package com.mobwal.walker.beautil.v1.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Класс реализующий интерфейс SensorEventListener
 */
public abstract class SensorActivity extends AppCompatActivity
    implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private Sensor accelerometer;
    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this, magnetometer);
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == magnetometer) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
            lastMagnetometerSet = true;
        } else if (sensorEvent.sensor == accelerometer) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            lastAccelerometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            onDegreesChanged(azimuthInDegrees);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Обработчик изменение положения в пространстве
     * @param azimuthInDegrees горизонтальное смещение в градусах
     */
    public abstract void onDegreesChanged(float azimuthInDegrees);
}
