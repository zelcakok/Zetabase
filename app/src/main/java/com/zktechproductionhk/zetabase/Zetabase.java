package com.zktechproductionhk.zetabase;

import android.content.Context;

public class Zetabase<T extends Object> {
    private static final String TAG = "[Zetabase]";
    private static Zetabase instance;
    private Context context;
    private StorageAdapter<T> storageAdapter;

    public static Zetabase getInstance(Context context) {
        if (instance == null) instance = new Zetabase(context);
        return instance;
    }

    private Zetabase(Context context) {
        this.context = context;
        this.storageAdapter = StorageAdapter.getInstance(context);
    }

    public void write(String path, T data) {
        storageAdapter.write(path, data);
    }

    public void append(String path, T data) {
        storageAdapter.append(path, data);
    }

    public DataSnapshot<T> read(String path) {
        return storageAdapter.read(path);
    }

    public String showInfo() {
        return storageAdapter.showInfo();
    }

    public String toJSON() {
        return storageAdapter.toJSON();
    }

    public void parseJSON(String json) {
        storageAdapter.fromJSON(context, json);
    }
}