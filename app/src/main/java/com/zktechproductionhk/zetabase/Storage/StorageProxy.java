package com.zktechproductionhk.zetabase.Storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class StorageProxy<T extends Object> implements Serializable {
    private static String TAG = "[StorageProxy]";
    private HashMap database;

    public StorageProxy() {
        database = new HashMap<>();
        HashMap<String, T> root = new HashMap<>();
        root.put("val", null);
        database.put("/", root);
    }

    private StorageProxy<T> setDatabase(HashMap database) {
        this.database = database;
        return this;
    }

    public void write(final String path, final T data) {
        String node = null;
        HashMap ptr = (HashMap) database.get("/");
        ArrayList<String> nodes = parse(path);
        if (nodes != null) {
            HashMap<String, T> val = new HashMap<>();
            val.put("val", data);
            for (String key : nodes) {
                node = key;
                if (nodes.indexOf(node) == nodes.size() - 1) break;
                if (!ptr.containsKey(key)) {
                    HashMap newNode = new HashMap();
                    ptr.put(node, newNode);
                }
                ptr = (HashMap) ptr.get(key);
            }
            ptr.put(node, val);
        } else {
            ptr.put("val", data);
        }
    }

    public DataSnapshot read(String path) {
        HashMap<String, T> ptr = (HashMap<String, T>) database.get("/");
        ArrayList<String> nodes = parse(path);
        if (nodes != null) {
            for (String key : nodes)
                if (ptr.containsKey(key))
                    ptr = (HashMap<String, T>) ptr.get(key);
        }
        return new DataSnapshot<T>(ptr);
    }

    public String toJSON() {
        return new GsonBuilder().serializeNulls().create().toJson(this.database);
    }

    public static <T extends Object> StorageProxy<T> fromJSON(String json) {
        Type type = new TypeToken<HashMap<String, HashMap<String, T>>>() {
        }.getType();
        HashMap<String, HashMap<String, T>> database = new Gson().fromJson(json, type);
        return new StorageProxy<T>().setDatabase(database);
    }

    protected static ArrayList<String> parse(String path) {
        if (path.equals("/")) return null;
        ArrayList<String> nodes = new ArrayList<>();
        for (String node : path.split("/")) if (node.length() > 1) nodes.add(node);
        return nodes;
    }
}
