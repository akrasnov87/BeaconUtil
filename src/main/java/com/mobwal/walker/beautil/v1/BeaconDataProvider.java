package com.mobwal.walker.beautil.v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

public interface BeaconDataProvider {

    /**
     * Авторизация
     *
     * @param login логин
     * @param password пароль
     * @return токен атворизации для отправик текущего местоположения
     */
    String auth(@NonNull String login, @NonNull String password);

    /**
     * Получение списка beacon, которые будут использоваться приложением
     *
     * @param token токен авторизации полученный после вызова метода auth
     * @return
     */
    @Nullable
    wBeacon[] getBeacons(@NonNull String token);

    /**
     * Отправка данных о местоположении
     *
     * @param token токен авторизации полученный после вызова метода auth
     */
    boolean push(@NonNull String token);
}
