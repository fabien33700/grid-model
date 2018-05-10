package org.flatbase.model;

import java.util.HashMap;
import java.util.Map;

public class Row extends HashMap<String, Object> {
    public Row() {
        super();
    }

    public Row(Map<? extends String, ?> m) {
        super(m);
    }
}
