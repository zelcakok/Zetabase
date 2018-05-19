package com.zktechproductionhk.zetabase.Storage.InternalStorage;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileProxy {
    private static FileProxy instance;
    private Context context;
    private String filename;
    public static File file;

    public enum State {
        READ, WRITE, CLOSE
    }

    public static FileProxy getInstance(Context context) {
        if (instance == null) instance = new FileProxy(context);
        return instance;
    }

    private FileProxy(Context context) {
        this.context = context;
        this.filename = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    public void write(Object data) {
        try {
            FileOutputStream stream = context.openFileOutput(this.filename, Context.MODE_PRIVATE);
            stream.write((byte[]) data);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File open(State state) {
        file = new File(context.getFilesDir(), this.filename);
        if (file.exists()) return file;
        try {
            file.createNewFile();
            return changeState(state, file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete() {
        file = new File(context.getFilesDir(), this.filename);
        if (file.exists()) file.delete();
    }

    private File changeState(State state, File file) {
        switch (state) {
            case READ:
                return readState(file);
            case WRITE:
                return writeState(file);
            case CLOSE:
                return closeState(file);
            default:
                return null;
        }
    }

    private File writeState(File file) {
        file.setWritable(true);
        file.setReadable(false);
        file.setExecutable(false);
        return file;
    }

    private File readState(File file) {
        file.setWritable(false);
        file.setReadable(true);
        file.setExecutable(false);
        return file;
    }

    private File closeState(File file) {
        file.setWritable(false);
        file.setReadable(false);
        file.setExecutable(false);
        return file;
    }

    private class FileNotExistException extends Exception {
        FileNotExistException() {
            super("File does not exist");
        }

        FileNotExistException(String msg) {
            super(msg);
        }
    }
}
