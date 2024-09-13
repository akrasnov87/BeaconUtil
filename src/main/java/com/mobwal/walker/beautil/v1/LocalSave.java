package com.mobwal.walker.beautil.v1;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * Класс для локального сохранения данных
 */
public class LocalSave {

    private final SimpleFileManager simpleFileManager;
    private final String fileName;

    public LocalSave(@NonNull Context context, @NonNull String localSaveName) {
        fileName = localSaveName;
        simpleFileManager = new SimpleFileManager(context.getCacheDir(), "local-save");
    }

    public void writeLine(@NonNull byte[] bytes) throws IOException {
        simpleFileManager.writeBytes(fileName, bytes, true);
        simpleFileManager.writeBytes(fileName, "\n".getBytes(), true);
    }

    @Nullable
    public String[] readLines() throws IOException {
        byte[] bytes = simpleFileManager.readPath(fileName);

        if(bytes != null) {
            String data = new String(bytes);
            String[] lines = data.split("\n");
            return lines;
        }

        return null;
    }

    public boolean isEmpty() throws IOException {
        if(simpleFileManager.exists(fileName)) {
            byte[] bytes = simpleFileManager.readPath(fileName);
            return bytes.length == 0;
        }

        return true;
    }

    public void truncate() throws IOException {
        if(simpleFileManager.exists(fileName)) {
            simpleFileManager.truncate(fileName);
        }
    }

    public void clear() {
        simpleFileManager.deleteFolder();
    }
}
