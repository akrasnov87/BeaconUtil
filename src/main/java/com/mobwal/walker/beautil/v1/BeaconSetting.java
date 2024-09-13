package com.mobwal.walker.beautil.v1;

/**
 * Настройки фильтрации beacon
 */
public class BeaconSetting {
    /**
     * Интервал времени в миллисекундах для получения сигнала от beacon
     */
    public int interval = 5000;

    /**
     * Изменение уровня мощности сигнала для получения beacon
     */
    public int biasTxPower = 5;

    /**
     * Изменение дистанции в метрах для получения beacon
     */
    public double biasDistance = 0.05;

    /**
     * Дистанция для фильтрации beacon
     */
    public double distance = 10;
}
