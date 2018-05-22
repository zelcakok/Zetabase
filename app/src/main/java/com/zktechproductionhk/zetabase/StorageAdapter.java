package com.zktechproductionhk.zetabase;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.TreeMap;

import static com.zktechproductionhk.zetabase.SAConfig.DEFAULT_EMPTY_KEY;
import static com.zktechproductionhk.zetabase.SAConfig.DEFAULT_VALUE_KEY;
import static com.zktechproductionhk.zetabase.SAConfig.NODE_TYPE;
import static com.zktechproductionhk.zetabase.SAConfig.SEPARATER;
import static com.zktechproductionhk.zetabase.SAConfig.StorageNode;

class StorageAdapter<T extends Object> implements Serializable {
    private static final String TAG = "[StorageProxy]";
    private static StorageAdapter instance;

    private Context context;
    private TreeMap database;
    private DataStore dataStore;
    private String curKey = DEFAULT_EMPTY_KEY;

    private Object syncLock = new Object();

    private StorageAdapter(Context context) {
        this.context = context;
        database = new TreeMap<>();
        TreeMap<String, T> root = new TreeMap<>();
        root.put(DEFAULT_VALUE_KEY, null);
        database.put("/", root);
        dataStore = DataStore.getInstance();
    }

    public static StorageAdapter getInstance(Context context) {
        if (instance == null) instance = new StorageAdapter(context);
        return instance;
    }

    public void write(final String path, final T data) {
        Goods<T> goods = new Goods<>(data);
        dataStore.registerGoods(goods);
        TreeMap<String, String> value = new TreeMap<>();
        value.put(DEFAULT_VALUE_KEY, goods.getSlotId());
        StorageNode destNode = traversal(path, NODE_TYPE.PARENT, true);
        destNode.node.put(destNode.path, destNode.path == DEFAULT_VALUE_KEY ? goods.getSlotId() : value);
    }

    public void append(final String path, final T data) {
        new Thread(new Append(path, data)).run();
    }

    public DataSnapshot<T> read(final String path) {
        StorageNode node = traversal(path, NODE_TYPE.MATCH, false);
        return new DataSnapshot<>(node.node);
    }

    public String toJSON() {
        String dbJSON = new GsonBuilder().serializeNulls().create().toJson(this.database);
        String dsJSON = this.dataStore.toJSON();
        return dbJSON + SEPARATER + dsJSON;
    }

    public static <T extends Object> StorageAdapter<T> fromJSON(Context context, String json) {
        String[] token = json.split(SEPARATER);
        Type type = new TypeToken<TreeMap<String, TreeMap<String, T>>>() {
        }.getType();
        TreeMap<String, TreeMap<String, T>> database = new Gson().fromJson(token[0].toString(), type);
        StorageAdapter<T> sa = new StorageAdapter<T>(context).setDatabase(database);
        sa.setDataStore(token[1]);
        return sa;
    }

    public String showInfo() {
        return this.database.toString() + ", " + dataStore.showInfo();
    }

    private StorageNode traversal(final String path, NODE_TYPE type, @Nullable Boolean buildPath) {
        boolean build = buildPath == null ? false : buildPath;
        String destNode = DEFAULT_VALUE_KEY;
        TreeMap ptr = (TreeMap) database.get("/");
        ArrayList<String> nodes = parse(path);
        if (nodes != null) {
            for (String key : nodes) {
                destNode = key;
                if (type == NODE_TYPE.PARENT && nodes.indexOf(destNode) == nodes.size() - 1) break;
                if (build && !ptr.containsKey(key)) {
                    TreeMap newNode = new TreeMap();
                    ptr.put(destNode, newNode);
                }
                if (ptr.get(key) instanceof TreeMap)
                    ptr = (TreeMap) ptr.get(key);
            }
        }
        return new StorageNode(ptr, destNode);
    }

    private void generateKey() {
        new Thread(new GenerateKey()).run();
    }

    protected static ArrayList<String> parse(String path) {
        if (path.equals("/")) return null;
        ArrayList<String> nodes = new ArrayList<>();
        for (String node : path.split("/")) if (node.length() > 1) nodes.add(node);
        return nodes;
    }

    private class GenerateKey implements Runnable {
        @Override
        public void run() {
            synchronized (syncLock) {
                try {
                    curKey = "S" + String.valueOf(System.currentTimeMillis());
                    Thread.sleep(1);
                    syncLock.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Append implements Runnable {
        private String path;
        private T data;

        public Append(String path, T data) {
            this.path = path;
            this.data = data;
        }

        @Override
        public synchronized void run() {
            synchronized (syncLock) {
                try {
                    generateKey();
                    while (curKey.equals(DEFAULT_EMPTY_KEY)) syncLock.wait();
                    Goods<T> goods = new Goods<>(data);
                    dataStore.registerGoods(goods);
                    StorageNode node = traversal(path, NODE_TYPE.MATCH, true);
                    node.node.put(curKey, goods.getSlotId());
                    curKey = DEFAULT_EMPTY_KEY;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private StorageAdapter<T> setDatabase(TreeMap database) {
        this.database = database;
        return this;
    }

    private StorageAdapter<T> setDataStore(String json) {
        this.dataStore.fromJSON(json);
        return this;
    }
}
