package com.zktechproductionhk.zetabase;

import android.content.Context;
import android.util.Log;

import com.zktechproductionhk.zetabase.Storage.DataSnapshot;
import com.zktechproductionhk.zetabase.Storage.StorageProxy;

public class Zetabase {
    private static final String TAG = "[Zetabase]";
    private static Zetabase instance;
    private Context context;
    private com.zktechproductionhk.zetabase.Storage.InternalStorage.FileWriter FileWriter;

    public static Zetabase getInstance(Context context) {
        if (instance == null) instance = new Zetabase(context);
        return instance;
    }

    private Zetabase(Context context) {
        this.context = context;
        FileWriter = FileWriter.getInstance(context);

        StorageProxy<String> sp = new StorageProxy<>();
        sp.write("/", "ABCDE");

        DataSnapshot<String> dataSnapshot = sp.read("/");

        Log.d(TAG, dataSnapshot.value());
    }
}