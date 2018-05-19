package com.zktechproductionhk.zetabase.Storage.InternalStorage;

import android.content.Context;

public class FileWriter {
    private static String TAG = "[FileWriter]";
    private static FileWriter instance;
    private Context context;
    private FileProxy fileProxy;

    public static FileWriter getInstance(Context context) {
        if (instance == null) instance = new FileWriter(context);
        return instance;
    }

    private FileWriter(Context context) {
        this.context = context;
        this.fileProxy = FileProxy.getInstance(context);
    }

    public void write(Object data) {
        fileProxy.open(FileProxy.State.WRITE);
        fileProxy.write(data);
    }

    public void wipeStorage(){
        fileProxy.delete();
    }
}
