package com.zktechproductionhk.zetabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import static com.zktechproductionhk.zetabase.SAConfig.DEFAULT_VALUE_KEY;

public class DataSnapshot<T extends Object> implements Serializable {
    private TreeMap<String, T> dataSnapshot;

    public DataSnapshot() {
        dataSnapshot = new TreeMap<>();
    }

    public DataSnapshot(TreeMap dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }

    public DataSnapshot<T> get(String path) {
        TreeMap<String, T> ptr = dataSnapshot;
        ArrayList<String> nodes = StorageAdapter.parse(path);
        if (nodes == null) return this;
        for (String node : nodes)
            if (ptr.get(node) instanceof TreeMap && ptr.containsKey(node))
                ptr = (TreeMap<String, T>) ptr.get(node);
        return new DataSnapshot<>(ptr);
    }

    public T value() {
        return (T) DataStore.getInstance().retrieveGoods(String.valueOf(dataSnapshot.get(DEFAULT_VALUE_KEY))).getItem();
    }

    public DataSnapshot<T> dataSnapshot() {
        return new DataSnapshot(dataSnapshot);
    }

    @Override
    public String toString() {
        return dataSnapshot.toString();
    }
}
