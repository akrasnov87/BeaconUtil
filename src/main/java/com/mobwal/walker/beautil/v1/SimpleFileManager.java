package com.mobwal.walker.beautil.v1;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Простой файловый менеджер
 */
class SimpleFileManager {
    public static final String TAG = "SIMPLE_FILE_MANAGER";
    private static final int BUFFER_SIZE = 2048;

    private final File mEnvironment;

    public File getEnvironment() {
        return mEnvironment;
    }

    /**
     * Хранение данных
     *
     * @param environment директория из context.getFileDir() | context.getCacheDir()
     */
    public SimpleFileManager(@NonNull File environment) {
        mEnvironment = environment;
    }

    /**
     * Хранение данных
     *
     * @param environment директория из context.getFileDir() | context.getCacheDir()
     * @param subFolder субдиректория
     */
    public SimpleFileManager(@NonNull File environment, @NonNull String subFolder) {
        this(new File(environment, subFolder));
    }

    /**
     * Запись байтов в файловую систему
     *
     * @param fileName имя файла
     * @param bytes    массив байтов
     * @throws IOException исключение
     */
    public void writeBytes(@NonNull String fileName, @NonNull byte[] bytes, boolean append) throws IOException {
        if (!mEnvironment.exists()) {
            if(!mEnvironment.mkdirs()) {
                Log.d(TAG, "Каталог " + mEnvironment.getName() + " не создан");
            }
        }

        File file = new File(mEnvironment, fileName);

        FileOutputStream outputStream = new FileOutputStream(file, append);
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        bos.write(bytes, 0, bytes.length);
        bos.flush();
        bos.close();
    }

    /**
     * Чтение информации о файле
     *
     * @param fileName имя файла
     * @return возвращается массив байтов
     * @throws IOException исключение
     */
    public byte[] readPath(@NonNull String fileName) throws IOException {
        File file = new File(mEnvironment, fileName);
        if (file.exists()) {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            byte[] data = new byte[BUFFER_SIZE];
            int count;
            while ((count = bis.read( data, 0, BUFFER_SIZE)) != -1) {
                buf.write(data, 0, count);
            }
            buf.flush();
            buf.close();
            bis.close();
            inputStream.close();

            return buf.toByteArray();
        } else {
            return null;
        }
    }

    public void truncate(@NonNull String fileName) throws IOException {
        File file = new File(mEnvironment, fileName);
        if (file.exists()) {
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(("").getBytes());
            writer.close();
        }
    }

    /**
     * Копирование файлов
     * @param source источник
     * @param target назначение
     */
    public void copy(@NonNull File source, @NonNull File target) {
        try {
            try (InputStream in = new FileInputStream(source)) {
                try (OutputStream out = new FileOutputStream(target)) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException ignored) {

        }
    }

    /**
     * Доступен ли файл
     *
     * @param fileName имя файла
     * @return возвращается доступен ли файл
     */
    public boolean exists(@NonNull String fileName) {
        File file = new File(mEnvironment, fileName);
        return file.exists();
    }

    /**
     * удаление файла
     *
     * @param fileName имя файла
     */
    public void deleteFile(@NonNull String fileName) {
        if (!mEnvironment.exists()) {
            Log.d(TAG, "Корневая директория " + mEnvironment.getName() + " не найдена.");
        }
        File file = new File(mEnvironment, fileName);
        if (file.exists()) {
            deleteRecursive(file);
        } else {
            Log.d(TAG, "Файл " + fileName + " в директории " + mEnvironment.getName() + " не найден.");
        }
    }

    /**
     * очистка папки
     */
    public void deleteFolder() {
        if (mEnvironment.exists()) {
            deleteRecursive(mEnvironment);
        } else {
            Log.d(TAG, "Директория " + mEnvironment.getName() + " не найдена.");
        }
    }

    /**
     * удаление объекта File
     *
     * @param fileOrDirectory файл или директория
     */
    public static boolean deleteRecursive(@NonNull File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : Objects.requireNonNull(fileOrDirectory.listFiles())) {
                if (!deleteRecursive(child)) {
                    Log.d(TAG, "Директория " + child.getName() + " не удалена.");
                }
            }
        }
        return fileOrDirectory.delete();
    }
}
