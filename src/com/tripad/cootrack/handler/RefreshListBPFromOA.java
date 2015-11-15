/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.handler;

import com.tripad.cootrack.utility.OpenApiUtils;
import com.tripad.cootrack.utility.ResponseResultToDB;
import java.util.Map;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.base.exception.OBException;

/**
 *
 * @author mfachmirizal
 */
public class RefreshListBPFromOA {
    protected JSONObject execute(Map<String, Object> parameters, String data) {
        String hasil = "";
        OpenApiUtils utils = new OpenApiUtils();
        JSONObject hasilRetrieve  = utils.requestStringListChildAccount(null);
        //ArrayList<String> tempIdDataServer = new ArrayList<String>();
        try {    
            hasil = hasilRetrieve.get("msg").toString();
            if (hasil.length() == 0) {
                //Retrieve seluruh Data dari OpenAPi Ke BP
                new ResponseResultToDB().validateBPList(hasilRetrieve);
                
            }
            //new CustomJsonErrorResponse("5555", "Fatal protocol violation : "+e.getMessage()).getErrResponse();
            JSONObject json = new JSONObject();
            json.put("jawaban", hasil);
            
            // and return it
            return json;
        } catch (Exception e) {
            throw new OBException(e);
        } catch (Throwable ex) {
            throw new OBException(ex);
        }
    }
}
