package com.uptc.edu.co.tictactoe.Network;

import java.util.Map;

public class Request {
    private String type;
    private Map<String, Object> params;

    public Request(String type, Map<String, Object> params) {
        this.type = type;
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getStringData(String key) {
        Object value = params.get(key);
        return value != null ? value.toString() : null;
    }

    public Integer getIntData(String key) {
        Object value = params.get(key);
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
        Object value = params.get(key);
        if (value != null) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return Boolean.parseBoolean(value.toString());
        }
        return null;
    }
} 