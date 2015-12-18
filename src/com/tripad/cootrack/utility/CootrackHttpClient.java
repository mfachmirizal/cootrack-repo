package com.tripad.cootrack.utility;

//import org.apache.commons.httpclient.*;
//import org.apache.commons.httpclient.methods.*;
//import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.impl.client.DefaultHttpClient;

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


public class CootrackHttpClient {
    final HttpParams httpParams = new BasicHttpParams();
    private HttpClient client = null;
    private HttpUriRequest request = null;
    private HttpResponse response = null;
    private static boolean inProcess = false;
    public CootrackHttpClient() {
    }
    
    //private static String url = "http://www.apache.org/";
    
    
    public String post(String url) {
        inProcess = true;
        String result ="";
        // Create an instance of HttpClient.
        
        //HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
        //client = new DefaultHttpClient(httpParams);
        
        client = new DefaultHttpClient(httpParams);
        //client.setConnectionTimeout(1000*63);
        
        // Create a method instance.
        //GetMethod method = new GetMethod(url);
        request = new HttpGet(url);
      
        // Provide custom retry handler is necessary
//        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
//                new DefaultHttpMethodRetryHandler(1, false));
        
        try {
            // Execute the method.
            //int statusCode = client.execute(request);
          response = client.execute(request);
            
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                //System.err.println("Method failed: " + method.getStatusLine());
                //throw new OBException("Error !, status code : " + statusCode
                result = new CustomJsonErrorResponse("5555", "Error !, status : ("+response.getStatusLine().getStatusCode()+") "+response.getStatusLine().getReasonPhrase()).getStringErrResponse();
                        /* "{\"ret\": 55555, " +                        
                "    \"msg\": \"Error !, status : ("+statusCode+") "+HttpStatus.getStatusText(statusCode)+" \"}"; */
                return result;
            }
            
            // Read the response body.
            StringBuilder constructOutput = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output;
            while ((output = br.readLine()) != null) {
		constructOutput.append(output);
	    }
            
            // Deal with the response.
            result = constructOutput.toString();//new String(responseBody);
            
            System.out.println(result);
            if( response.getEntity() != null ) {
              response.getEntity().consumeContent();
            }
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
            
        } catch (IOException e) {
            inProcess = false;
            result = new CustomJsonErrorResponse("5555", "Gagal Terhubung dengan server,Harap Cek Koneksi internet anda. Target : "+e.getMessage() ).getStringErrResponse(); 
                    /* "{\"ret\": 55555, " +
                        "    \"msg\": \"Gagal Terhubung dengan server,Harap Cek Koneksi internet anda. Target : "+e.getMessage()+" \"}"; */
        } 
        inProcess = false;
        return (result);
    }
    
    public void cancel() {
        if (request != null) {
            request.abort();
        }
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