package com.tripad.cootrack.utility;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import java.io.*;
import org.openbravo.base.exception.OBException;

public class CootrackHttpClient {
    
    public CootrackHttpClient() {
    }
    
    //private static String url = "http://www.apache.org/";
    
    public String post(String url) {
        String result ="";
        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();
        
        // Create a method instance.
        GetMethod method = new GetMethod(url);
        
      
        // Provide custom retry handler is necessary
//        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//                new DefaultHttpMethodRetryHandler(1, false));
        
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);
            
            if (statusCode != HttpStatus.SC_OK) {
                //System.err.println("Method failed: " + method.getStatusLine());
                //throw new OBException("Error !, status code : " + statusCode
                result = "{\"ret\": 55555, " +
                        "    \"msg\": \"Error !, status : ("+statusCode+") "+HttpStatus.getStatusText(statusCode)+" \"}";
                return result;
            }
            
            // Read the response body.
            byte[] responseBody = method.getResponseBody();
            
            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary data
            result = new String(responseBody);
            //System.out.println(result);
            
        } catch (HttpException e) {
            //System.err.println("Fatal protocol violation: " + e.getMessage());
            //e.printStackTrace();
            //throw new OBException("Tidak dapat Tersambung ke server API !"+e.getMessage());
            result = "{\"ret\": 55555, " +
                        "    \"msg\": \"Fatal protocol violation : "+e.getMessage()+" \"}";
        } catch (IOException e) {
            // System.err.println("Fatal transport error: " + e.getMessage());
            //e.printStackTrace();
            result = "{\"ret\": 55555, " +
                        "    \"msg\": \"Gagal Terhubung dengan server,Harap Cek Koneksi internet anda. Target : "+e.getMessage()+" \"}";
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return (result);
    }
    
}