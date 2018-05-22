package com.zktechproductionhk.zetabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

class DataStore {
    private static String TAG = "[DataStore]";
    private static DataStore instance;
    private HashMap<String, Object> inventory;

    private static String DEFAULT_EMPTY_KEY = "NUL";

    private String curKey = DEFAULT_EMPTY_KEY;
    private Object syncLock = new Object();

    private DataStore() {
        this.inventory = new HashMap<>();
    }

    private DataStore(HashMap<String, Object> inventory) {
        this.inventory = inventory;
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public static DataStore getInstance(HashMap<String, Object> inventory) {
        if (instance == null) instance = new DataStore(inventory);
        return instance;
    }

    public String register(Object data) {
        String hashKey = "DS_" + System.currentTimeMillis();
        inventory.put(hashKey, data);
        return hashKey;
    }

    private void generateKey() {
        new Thread(new GenerateKey()).run();
    }

    public void registerGoods(Goods goods) {
        new Thread(new RegisterGoods(goods)).run();
    }

    public Goods retrieveGoods(String slotId) {
        if (!inventory.containsKey(slotId)) return null;
        return new Goods(slotId, inventory.get(slotId));
    }

    public String showInfo() {
        return this.inventory.toString();
    }

    public String toJSON() {
        return new GsonBuilder().serializeNulls().create().toJson(this.inventory);
    }

    public static <T extends Object> DataStore fromJSON(String json) {
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        HashMap<String, Object> inventory = new Gson().fromJson(json, type);
        return new DataStore(inventory);
    }

    private class GenerateKey implements Runnable {
        @Override
        public void run() {
            synchronized (syncLock) {
                try {
                    curKey = "D" + String.valueOf(System.currentTimeMillis());
                    Thread.sleep(1);
                    syncLock.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class RegisterGoods implements Runnable {
        private Goods goods;

        public RegisterGoods(Goods goods) {
            this.goods = goods;
        }

        @Override
        public void run() {
            synchronized (syncLock) {
                try {
                    generateKey();
                    while (curKey == DEFAULT_EMPTY_KEY) syncLock.wait();
                    inventory.put(curKey, goods.unpack());
                    goods.setSlotId(curKey);
                    curKey = DEFAULT_EMPTY_KEY;
                    syncLock.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
