package io.pallas.core.view;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Model extends HashMap<String, Object> {
    public Model() {
        super();
    }

    public Model(final Map<String, Object> value) {
        this();
        putAll(value);
    }

    public Model set(final String key, final Object value) {
        put(key, value);

        return this;
    }

    /**
     * @return
     */
    public Map<String, String> toStringMap() {

        final Map<String, String> map = new HashMap<String, String>();

        for (final Map.Entry<String, Object> entry : this.entrySet()) {

            map.put(entry.getKey(), entry.getValue().toString());
        }

        return map;
    }

    public Model setAll(final Map<String, Object> data) {

        putAll(data);

        return this;
    }
}
