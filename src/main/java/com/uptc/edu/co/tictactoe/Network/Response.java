package com.uptc.edu.co.tictactoe.Network;

import java.util.Map;

public class Response {
    private String type;
    private Map<String, Object> data;

    public Response(String type, Map<String, Object> data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getStringData(String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    public Integer getIntData(String key) {
        Object value = data.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Boolean getBooleanData(String key) {
        Object value = data.get(key);
        if (value != null) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return Boolean.parseBoolean(value.toString());
        }
        return null;
    }
}