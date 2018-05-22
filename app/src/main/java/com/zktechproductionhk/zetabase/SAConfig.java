package com.zktechproductionhk.zetabase;

import java.util.TreeMap;

class SAConfig {
    protected static final String DEFAULT_VALUE_KEY = "VAL";
    protected static final String DEFAULT_EMPTY_KEY = "NUL";
    protected static final String SEPARATER = "&SEP!";

    protected enum NODE_TYPE {
        MATCH(0), PARENT(1);

        public int value;

        NODE_TYPE(int value) {
            this.value = value;
        }
    }

    protected static class StorageNode {
        public TreeMap node;
        public String path;

        public StorageNode(TreeMap node, String path) {
            this.node = node;
            this.path = path;
        }
    }
}
