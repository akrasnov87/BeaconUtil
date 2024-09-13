package com.mobwal.walker.beautil.v1;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * класс для хранения меток
 */
public class wBeaconBufferManager {

    public static final String TAG = "BEACON_BUFFER_MANAGER";
    public static final String BUFFER_FILE_NAME = "beacon.buffer";

    /**
     * Хранение меток в регионе
     */
    private HashMap<String, List<wBeacon>> buffers;
    private final Context context;

    public static String getHashMapId(@NonNull Beacon beacon) {

        StringBuilder stringBuilder = new StringBuilder();

        for (Identifier identifier:
                beacon.getIdentifiers()) {
            stringBuilder.append(identifier.toString()).append("_");
        }

        return stringBuilder.toString();
    }

    public wBeaconBufferManager(@NonNull Context context) {
        this.context = context;

        buffers = new HashMap<>();

        SimpleFileManager simpleFileManager = new SimpleFileManager(context.getCacheDir());
        if(simpleFileManager.exists(BUFFER_FILE_NAME)) {
            try {
                byte[] bytes = simpleFileManager.readPath(BUFFER_FILE_NAME);

                buffers = new Gson().fromJson(new String(bytes), buffers.getClass());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Добавление метки
     *
     * @param beacon
     */
    public void add(@NonNull Beacon beacon) {
        String hashMapId = getHashMapId(beacon);
        List<wBeacon> list;

        if(buffers.containsKey(hashMapId)) {
            list = buffers.get(hashMapId);
            if(list != null) {
                list.add(new wBeacon(beacon));
            }
        } else {
            list = new ArrayList<>();
            list.add(new wBeacon(beacon));
        }

        buffers.put(hashMapId, list);
    }

    /**
     * Получение данных по метке
     *
     * @param beacon
     * @return
     */
    public List<wBeacon> get(@NonNull Beacon beacon) {
        String hashMapId = getHashMapId(beacon);

        if(buffers.containsKey(hashMapId)) {
            return buffers.get(hashMapId);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Проверка доступности метки
     *
     * @param beacon
     * @return
     */
    public boolean exists(@NonNull Beacon beacon) {
        String hashMapId = getHashMapId(beacon);

        return buffers.containsKey(hashMapId);
    }

    /**
     * Удаление метки
     *
     * @param beacon
     */
    public void remove(@NonNull Beacon beacon) {
        String hashMapId = getHashMapId(beacon);

        buffers.remove(hashMapId);
    }

    /**
     * Удаление меток по вхождению идентификатора
     * @param key ключ
     */
    public void removeContainsKey(@NonNull String key) {
        for (String s:
             buffers.keySet()) {
            if(s.contains(key)) {
                buffers.remove(s);
            }
        }
    }

    /**
     * Очистка данных
     */
    public void clearAll() {
        buffers.clear();

        SimpleFileManager simpleFileManager = new SimpleFileManager(this.context.getCacheDir());
        simpleFileManager.deleteFile(BUFFER_FILE_NAME);
    }

    /**
     * Сохранение данных в файловую систему
     */
    public void flush() {
        SimpleFileManager simpleFileManager = new SimpleFileManager(this.context.getCacheDir());
        try {
            simpleFileManager.writeBytes(BUFFER_FILE_NAME, bufferToString().getBytes(), false);
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    private String bufferToString() {
        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(buffers);
    }

    /**
     * Получение списка последних меток
     *
     * @param distance дистанция
     */
    public wBeacon[] getLastBeacons(Double distance) {
        List<wBeacon> results = new ArrayList<>();

        for (String key: buffers.keySet()) {
            List<wBeacon> list = buffers.get(key);
            if(list != null && !list.isEmpty()) {
                wBeacon item = list.get(0);

                if(distance != null) {
                    if(item.distance <= distance) {
                        results.add(item);
                    }
                } else {
                    results.add(item);
                }
            }
        }

        return results.toArray(new wBeacon[0]);
    }
}
