/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tripad.cootrack.utility;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author mfachmirizal
 */
public class CustomJsonErrorResponse {
    private String ret;
    private String msg;

    /**
     * Constructor untuk menginialisasi nilai return, dan message
     * @param paramRet
     * @param paramMsg 
     */
    public CustomJsonErrorResponse(String paramRet, String paramMsg) {
        ret= paramRet;
        msg= paramMsg; 
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
    
    /**
     * 
     * @return JSON String 
     */
    public String getStringErrResponse() {
        return "{\"ret\": "+ret+"," +
                        "\"msg\": \""+msg+"\"}";
    }
    
    public JSONObject getJSONErrResponse() throws JSONException{
        return new JSONObject("{\"ret\": "+ret+"," +"\"msg\": \""+msg+"\"}");
    }
}
