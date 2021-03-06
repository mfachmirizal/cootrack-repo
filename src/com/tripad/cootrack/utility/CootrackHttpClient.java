package com.tripad.cootrack.utility;

//import org.apache.commons.httpclient.*;
//import org.apache.commons.httpclient.methods.*;
//import java.io.*;
import com.tripad.cootrack.utility.exception.CustomJsonErrorResponseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.openbravo.base.exception.OBException;

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
  //private HttpClient client = null;
  private DefaultHttpClient client = null;
  private HttpUriRequest request = null;
  private HttpResponse response = null;
  private static boolean inProcess = false;
  private final int LAMA_TIMEOUT = 121;

  public CootrackHttpClient() {
  }

  // private static String url = "http://www.apache.org/";

  public String post(String url) {

    final HttpParams httpParams = new BasicHttpParams();
    inProcess = true;
    String result = "";
    // Create an instance of HttpClient.

    // HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
    // client = new DefaultHttpClient(httpParams);
    HttpConnectionParams.setConnectionTimeout(httpParams, (LAMA_TIMEOUT * 1000));
    HttpConnectionParams.setSoTimeout(httpParams, (LAMA_TIMEOUT * 1000));

    DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParams);
    defaultHttpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

    client = defaultHttpClient;
    // client.setConnectionTimeout(1000*63);

    // Create a method instance.
    // GetMethod method = new GetMethod(url);
    request = new HttpGet(url);

    // Provide custom retry handler is necessary
    // method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
    // new DefaultHttpMethodRetryHandler(1, false));

    try {
      // Execute the method.
      // int statusCode = client.execute(request);
      response = client.execute(request);

      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//        result = new CustomJsonErrorResponse("5555",
//            "Error !, status : (" + response.getStatusLine().getStatusCode() + ") "
//                + response.getStatusLine().getReasonPhrase()).getStringErrResponse();
          result = new CustomJsonErrorResponseException("5555",
            "Error !, status : (" + response.getStatusLine().getStatusCode() + ") "
                + response.getStatusLine().getReasonPhrase()).getStringErrResponse();
        return result;
      }

      // Read the response body.
      StringBuilder constructOutput = new StringBuilder();
      BufferedReader br = new BufferedReader(
          new InputStreamReader((response.getEntity().getContent())));
      String output;
      while ((output = br.readLine()) != null) {
        constructOutput.append(output);
      }

      // Deal with the response.
      result = constructOutput.toString();// new String(responseBody);

      //System.out.println(result);
      //asalnya is method shutdown ada disini

    } catch (SocketTimeoutException z) {
      // handle timeouts
      inProcess = false;
      result = new CustomJsonErrorResponseException("5555",
          "Unable to connect to the server. Please check your Internet connection : "
              + z.getMessage()).getStringErrResponse();
      //throw new CustomJsonErrorResponseException(result);

    } catch (IOException e) {
      inProcess = false;
//      result = new CustomJsonErrorResponse("5555",
//          "Unable to connect to the server. Please check your Internet connection. Target : "
//              + e.getMessage()).getStringErrResponse();
      result = new CustomJsonErrorResponseException("5555",
          "Unable to connect to the server. Please check your Internet connection. Target : "
              + e.getMessage()).getStringErrResponse();
    }
    inProcess = false;
    return (result);
  }

  public void shutdown() {
    try {
      if (response.getEntity() != null) {
        try {
          response.getEntity().consumeContent();
        } catch (IOException ex) {
          System.out.println("Masuk exception consumeContent : " + ex.getMessage());
          Logger.getLogger(CootrackHttpClient.class.getName()).log(Level.SEVERE, null, ex);
          throw new OBException("Error consumeContent ! : " + ex.getMessage());
        }
      }
      if (client != null) {
        client.getConnectionManager().shutdown();
      }
    } catch (Exception e) {
      //do nothing
    }

  }

  public void cancel() {
    if (request != null) {
      request.abort();
    }
  }

  @Deprecated
  public String post2(String urlParam) {
    String result = "";
    try {
      URL url = new URL(urlParam);
      URLConnection con = url.openConnection();
      InputStream in = con.getInputStream();
      String encoding = con.getContentEncoding();
      encoding = encoding == null ? "UTF-8" : encoding;
      result = IOUtils.toString(in, encoding);
    } catch (MalformedURLException ex) {
//      result = new CustomJsonErrorResponse("5555", "Server Url not valid. : " + ex.getMessage())
//          .getStringErrResponse();
        result = new CustomJsonErrorResponseException("5555", "Server Url not valid. : " + ex.getMessage())
          .getStringErrResponse();
    } catch (IOException ioe) {
//      result = new CustomJsonErrorResponse("5555",
//          "Unable to connect to the server. Please check your Internet connection. : "
//              + ioe.getMessage()).getStringErrResponse();
        result = new CustomJsonErrorResponseException("5555",
          "Unable to connect to the server. Please check your Internet connection. : "
              + ioe.getMessage()).getStringErrResponse();
    }

    return result;
  }

}
