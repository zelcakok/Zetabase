package com.zktechproductionhk.zetabase;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class FileProxy {
    private static String TAG = "[FileProxy]";
    private static FileProxy instance;
    private Context context;
    private String appName;
    private String filePath;
    private String filename;

    private FileProxy(Context context) {
        this.context = context;
        this.appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        this.filePath = context.getFilesDir().getPath().toString() + "/";
        this.filename = this.appName;
        init();
    }

    private FileProxy(Context context, String filename) {
        this.context = context;
        this.appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        this.filePath = context.getFilesDir().getPath().toString() + "/";
        this.filename = this.appName + "_" + filename;
        init();
    }

    public static FileProxy getInstance(Context context) {
        if (instance == null) instance = new FileProxy(context);
        return instance;
    }

    public static FileProxy getInstance(Context context, String filename) {
        if (instance == null) instance = new FileProxy(context, filename);
        return instance;
    }

    public String read() {
        if (!isFileExist()) return null;
        int bufSize;
        byte[] buffer = new byte[8];
        StringBuilder builder = new StringBuilder();
        FileInputStream stream = null;
        try {
            stream = context.openFileInput(this.filename);
            if (stream.available() == 0) return null;
            while (stream.available() > 0) {
                bufSize = stream.read(buffer);
                builder.append(new String(buffer, 0, bufSize));
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(stream);
        }
        return null;
    }

    public void write(Object data, @Nullable Boolean append) {
        FileOutputStream stream = null;
        try {
            stream = context.openFileOutput(this.filename, Context.MODE_PRIVATE | (append == null ? null : Context.MODE_APPEND));
            stream.write((byte[]) data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(stream);
        }
    }

    private void close(InputStream stream) {
        try {
            if (stream != null)
                stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(OutputStream stream) {
        try {
            if (stream != null)
                stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        File file = new File(context.getFilesDir(), this.filename);
        if (file.exists()) file.delete();
    }

    private boolean isFileExist() {
        return new File(this.filePath + this.filename).exists();
    }

    private void init() {
        if (!isFileExist()) {
            try {
                String path = this.filePath + this.filename;
                File file = new File(path);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



