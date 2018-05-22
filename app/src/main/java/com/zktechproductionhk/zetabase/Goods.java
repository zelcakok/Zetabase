package com.zktechproductionhk.zetabase;

class Goods<T extends Object> {
    private String slotId;
    private T item;

    public Goods(T item) {
        this.item = item;
    }

    public Goods(String slotId, T item) {
        this.slotId = slotId;
        this.item = item;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public T unpack() {
        return item;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "slotId='" + slotId + '\'' +
                ", item=" + item +
                '}';
    }
}
