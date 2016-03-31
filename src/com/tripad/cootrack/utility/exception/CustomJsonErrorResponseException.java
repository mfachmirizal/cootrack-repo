package com.tripad.cootrack.utility.exception;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class CustomJsonErrorResponseException extends Exception {
    private String ret;
    private String msg;
    //Parameterless Constructor
    public CustomJsonErrorResponseException() {
    }
    
    //with parameter for get JSONObject message
    public CustomJsonErrorResponseException(String paramRet, String paramMsg) {
        ret = paramRet;
        msg = paramMsg;
    }
    
    /**
     * @return the ret
     */
    public String getRet() {
        return ret;
    }
    
    /**
     * @param ret the ret to set
     */
    public void setRet(String ret) {
        this.ret = ret;
    }
    
    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }
    
    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    
    //Constructor that accepts a message
    public CustomJsonErrorResponseException(String message) {
        super(message);
    }
    
    public CustomJsonErrorResponseException(Throwable cause) {
        super(cause);
        //getLogger().error(cause.getMessage(), cause);
    }
    
    public JSONObject getJSONErrResponse() throws JSONException {
        return new JSONObject("{\"ret\": " + ret + "," + "\"msg\": \"" + msg + "\"}");
    }
    /**
     *
     * @return JSON String
     */
    public String getStringErrResponse() {
        return "{\"ret\": " + ret + "," + "\"msg\": \"" + msg + "\"}";
    }
    
}