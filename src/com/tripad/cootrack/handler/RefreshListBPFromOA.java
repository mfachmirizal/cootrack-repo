/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.handler;

import com.tripad.cootrack.utility.CootrackHttpClient;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.client.kernel.BaseActionHandler;

import com.tripad.cootrack.utility.OpenApiUtils;
import com.tripad.cootrack.utility.ResponseResultToDB;
import com.tripad.cootrack.utility.exception.CustomJsonErrorResponseException;
import org.openbravo.dal.core.OBContext;
import org.openbravo.model.ad.access.User;

/**
 *
 * @author mfachmirizal
 */
public class RefreshListBPFromOA extends BaseActionHandler {
  static User COOTRACK_USER = OBContext.getOBContext().getUser();
  
  static CootrackHttpClient con = new CootrackHttpClient();
  static OpenApiUtils utils = new OpenApiUtils(con,COOTRACK_USER);
    
  @SuppressWarnings("finally")
  protected JSONObject execute(Map<String, Object> parameters, String data) {
    String hasil = "";
    
    JSONObject json = new JSONObject();
    

    // ArrayList<String> tempIdDataServer = new ArrayList<String>();
    try {
      if (utils.getCurrentPassword() != null) {
        JSONObject hasilRetrieve = utils.requestListChildAccount(null);
        hasil = hasilRetrieve.get("msg").toString();

        if (hasil.length() == 0) {
          // Retrieve seluruh Data dari OpenAPi Ke BP
          new ResponseResultToDB(utils).validateBPList(hasilRetrieve);
        }
        
        
        json.put("jawaban", hasil);

      } else {
        json.put("jawaban", "This Openbravo user not synchronized with OpenAPI !");
      }
      // and return it
      return json;
    } catch (CustomJsonErrorResponseException jre) {
      System.out.println("MASUK JSONEXCEPTION");
      json.put("jawaban", jre.getMessage());
      return json;
    } catch (JSONException e) {
      System.out.println("MASUK JSONEXCEPTION");
      json.put("jawaban", e.getMessage());
      return json;
    } catch (Exception t) {
      json.put("jawaban", "Internal Error : " + t.getMessage());
      System.out.print("MASUK Throwable : Internal Error :");
      System.out.println(": " + t.getMessage());
      return json;
    } finally {
      con.shutdown();
      return json;
    }
  }
}
