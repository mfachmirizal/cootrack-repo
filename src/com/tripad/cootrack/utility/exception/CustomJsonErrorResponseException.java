package com.tripad.cootrack.utility.exception;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.apache.log4j.Logger;

public class CustomJsonErrorResponseException extends Exception {
    private String ret;
    private String msg;
    //Parameterless Constructor
    public CustomJsonErrorResponseException() {
        getLogger().error("CustomJsonException", this);
    }
    
    //with parameter for get JSONObject message
    public CustomJsonErrorResponseException(String paramRet, String paramMsg) {
        ret = paramRet;
        msg = paramMsg;
        getLogger().error("CustomJsonException", this);
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
        getLogger().error(message, this);
    }
    
    public CustomJsonErrorResponseException(Throwable cause) {
        super(cause);
        getLogger().error(cause.getMessage(), cause);
    }
    
    public JSONObject getJSONErrResponse() throws JSONException {
        String err = "{\"ret\": " + ret + "," + "\"msg\": \"" + msg + "\"}";
        getLogger().error(err, this);        
        return new JSONObject(err);
    }
    /**
     *
     * @return JSON String
     */
    public String getStringErrResponse() {
        String err = "{\"ret\": " + ret + "," + "\"msg\": \"" + msg + "\"}";
        getLogger().error(err, this);    
        return err;
    }
 
    /**
   * This method returns a logger which can be used by a subclass. The logger is specific for the
   * instance of the Exception (the subclass).
   * 
   * @return the class-specific Logger
   */
  protected Logger getLogger() {
    return Logger.getLogger(this.getClass());
  }
}