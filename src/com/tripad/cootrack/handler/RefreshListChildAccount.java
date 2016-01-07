package com.tripad.cootrack.handler;

//import com.tripad.cootrack.data.TmcListChildAcc;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.openbravo.base.exception.OBException;
import org.openbravo.client.kernel.BaseActionHandler;

import com.tripad.cootrack.utility.OpenApiUtils;
import com.tripad.cootrack.utility.ResponseResultToDB;
import java.util.ArrayList;
import java.util.HashMap;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;

public class RefreshListChildAccount extends BaseActionHandler {
    
    protected JSONObject execute(Map<String, Object> parameters, String data) {
        String hasil = "";
        JSONObject json = new JSONObject();
        OpenApiUtils utils = new OpenApiUtils();
        try {
            if (utils.getCurrentPassword() != null ) {
                JSONObject hasilRetrieve  = utils.requestListChildAccount(null);
                
//debug            hasil = hasilRetrieve.toString();
                hasil = hasilRetrieve.get("msg").toString();
                if (hasil.length() == 0) {
                    //new ResponseResultToDB().validateChildList(hasilRetrieve);
                }
                
                
                //json.put("jawaban", hasil);
                json.put("jawaban", "Fitur ini tidak digunakan !"); //hardcoded bahwa fitur ini tidak digunakan
                
            } else {
                json.put("jawaban", "This Openbravo user not synchronized with OpenAPI !");
            }
            // and return it
            
        } catch (JSONException e) {
            json.put("jawaban", e.getMessage());
        } catch (Throwable t) {
            json.put("jawaban", t.getMessage());
        }finally {
            return json;
        }
    }
}