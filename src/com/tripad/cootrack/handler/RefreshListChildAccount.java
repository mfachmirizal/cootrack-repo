package com.tripad.cootrack.handler;

import com.tripad.cootrack.data.TmcListChildAcc;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.openbravo.base.exception.OBException;
import org.openbravo.client.kernel.BaseActionHandler;

import com.tripad.cootrack.utility.OpenApiUtils;
import com.tripad.cootrack.utility.ResponseResultToDB;
import java.util.ArrayList;
import java.util.HashMap;
import org.codehaus.jettison.json.JSONArray;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;

public class RefreshListChildAccount extends BaseActionHandler {
    
    protected JSONObject execute(Map<String, Object> parameters, String data) {
        String hasil = "";
        
        //ArrayList<String> tempIdDataServer = new ArrayList<String>();
        try {
            OpenApiUtils utils = new OpenApiUtils();
            JSONObject hasilRetrieve  = utils.requestStringListChildAccount();
            
            hasil = hasilRetrieve.get("msg").toString();
            if (hasil.length() == 0) {
                new ResponseResultToDB().validateChildList(hasilRetrieve);
            }
            //new CustomJsonErrorResponse("5555", "Fatal protocol violation : "+e.getMessage()).getErrResponse();
            JSONObject json = new JSONObject();
            json.put("jawaban", hasil);
            
            // and return it
            return json;
        } catch (Exception e) {
            throw new OBException(e);
        }
    }
}