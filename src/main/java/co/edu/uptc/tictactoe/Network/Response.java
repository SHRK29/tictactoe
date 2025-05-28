package co.edu.uptc.tictactoe.Network;

import java.util.Map;

public class Response {
    private String action;
    private Map<String, String> data;

    public Response(String action, Map<String,String> data) {
        this.action = action;
        this.data = data;
        
    }
 

    public String getAction() {
        return action;
    }

    
    public Map<String,String>  getData() {
        return data;
    }
    
    

}
