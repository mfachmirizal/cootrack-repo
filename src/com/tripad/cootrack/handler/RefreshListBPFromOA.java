/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.handler;

import com.tripad.cootrack.utility.OpenApiUtils;
import com.tripad.cootrack.utility.ResponseResultToDB;
import java.util.Map;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.base.exception.OBException;
import org.openbravo.client.kernel.BaseActionHandler;

/**
 *
 * @author mfachmirizal
 */
public class RefreshListBPFromOA extends BaseActionHandler{
    protected JSONObject execute(Map<String, Object> parameters, String data) {
        String hasil = "";
        JSONObject json = new JSONObject();
        OpenApiUtils utils = new OpenApiUtils();
        
        //ArrayList<String> tempIdDataServer = new ArrayList<String>();
        try {
            JSONObject hasilRetrieve  = utils.requestListChildAccount(null);
            hasil = hasilRetrieve.get("msg").toString();
            System.out.println("HASIL : "+hasil);
            if (hasil.length() == 0) {
                //Retrieve seluruh Data dari OpenAPi Ke BP
                new ResponseResultToDB().validateBPList(hasilRetrieve);
                
                //JSONArray dataList = (JSONArray) hasilRetrieve.get("children");
                
            }
            //new CustomJsonErrorResponse("5555", "Fatal protocol violation : "+e.getMessage()).getErrResponse();
            
            json.put("jawaban", hasil);
            
            // and return it
            return json;
        } catch (JSONException e) {
            json.put("jawaban", e.getMessage());
        }finally {
            return json;
        }
    }
}
