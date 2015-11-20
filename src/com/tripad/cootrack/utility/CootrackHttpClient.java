package com.tripad.cootrack.utility;

//import org.apache.commons.httpclient.*;
//import org.apache.commons.httpclient.methods.*;
//import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.*;

//ganti methodnya dengan ini : http://stackoverflow.com/questions/5769717/how-can-i-get-an-http-response-body-as-a-string-in-java
/*
URL url = new URL("http://www.example.com/");
URLConnection con = url.openConnection();
InputStream in = con.getInputStream();
String encoding = con.getContentEncoding();
encoding = encoding == null ? "UTF-8" : encoding;
String body = IOUtils.toString(in, encoding);
System.out.println(body);
*/

import org.apache.commons.io.IOUtils;
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
                result = new CustomJsonErrorResponse("5555", "Error !, status : ("+statusCode+") "+HttpStatus.getStatusText(statusCode)).getStringErrResponse();
                        /* "{\"ret\": 55555, " +                        
                "    \"msg\": \"Error !, status : ("+statusCode+") "+HttpStatus.getStatusText(statusCode)+" \"}"; */
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
            result = new CustomJsonErrorResponse("5555", "Fatal protocol violation : "+e.getMessage()).getStringErrResponse(); 
                    /* "{\"ret\": 55555, " +
                        "    \"msg\": \"Fatal protocol violation : "+e.getMessage()+" \"}"; */
        } catch (IOException e) {
            // System.err.println("Fatal transport error: " + e.getMessage());
            //e.printStackTrace();
            result = new CustomJsonErrorResponse("5555", "Gagal Terhubung dengan server,Harap Cek Koneksi internet anda. Target : "+e.getMessage() ).getStringErrResponse(); 
                    /* "{\"ret\": 55555, " +
                        "    \"msg\": \"Gagal Terhubung dengan server,Harap Cek Koneksi internet anda. Target : "+e.getMessage()+" \"}"; */
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return (result);
    }
    
    public String post2 (String urlParam){ 
        String result ="";
        try {
            URL url = new URL(urlParam);
            URLConnection con = url.openConnection();
            
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            result = IOUtils.toString(in, encoding);
        } catch (MalformedURLException ex) {
            result = new CustomJsonErrorResponse("5555", "Url Server Tidak Valid. : "+ex.getMessage() ).getStringErrResponse(); 
        } catch (IOException ioe) {
            result = new CustomJsonErrorResponse("5555", "Gagal Terhubung dengan server,Harap Cek Koneksi internet anda. : "+ioe.getMessage() ).getStringErrResponse(); 
        }
   
        return result;
    }
    
}