package com.zktechproductionhk.zetabase.Storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class DataSnapshot<T extends Object> implements Serializable {
    private HashMap<String, T> dataSnapshot;

    public DataSnapshot() {
        dataSnapshot = new HashMap<>();
    }

    public DataSnapshot(HashMap dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }

    public DataSnapshot<T> get(String path) {
        HashMap<String, T> ptr = dataSnapshot;
        ArrayList<String> nodes = StorageProxy.parse(path);
        if (nodes == null) return this;
        for (String node : nodes)
            if (ptr.get(node) instanceof HashMap && ptr.containsKey(node))
                ptr = (HashMap<String, T>) ptr.get(node);
        return new DataSnapshot<>(ptr);
    }

    public T value() {
        return dataSnapshot.get("val");
    }

    public DataSnapshot<T> dataSnapshot() {
        return new DataSnapshot(dataSnapshot);
    }

    @Override
    public String toString() {
        return "DataSnapshot{" +
                "dataSnapshot=" + dataSnapshot +
                '}';
    }
}
