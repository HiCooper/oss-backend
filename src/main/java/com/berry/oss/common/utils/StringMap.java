package com.berry.oss.common.utils;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class StringMap {
    private Map<String, Object> map;

    public StringMap() {
        this(new HashMap<>());
    }

    public StringMap(Map<String, Object> map) {
        this.map = map;
    }

    public StringMap put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public StringMap putNotBlank(String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
        return this;
    }

    public StringMap putNotNull(String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
        return this;
    }


    public StringMap putWhen(String key, Object val, boolean when) {
        if (when) {
            map.put(key, val);
        }
        return this;
    }

    public StringMap putAll(Map<String, Object> map) {
        this.map.putAll(map);
        return this;
    }

    public StringMap putAll(StringMap map) {
        this.map.putAll(map.map);
        return this;
    }

    public int size() {
        return map.size();
    }

    public Map<String, Object> map() {
        return this.map;
    }

    public Object get(String key) {
        return map.get(key);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public String jsonString() {
        return JSON.toJSONString(this);
    }

}
