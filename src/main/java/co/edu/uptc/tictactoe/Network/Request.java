package co.edu.uptc.tictactoe.Network;

import java.util.Map;

public class Request{
    private String type;
    private Map<String, String> parameters;

    public Request(String type, Map<String, String> string) {
        this.type = type;
        this.parameters = string;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getData(String key) {
       return parameters.get(key);
    }
}