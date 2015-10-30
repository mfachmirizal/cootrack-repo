package com.tripad.cootrack.handler;

import com.tripad.cootrack.data.TmcListChildAcc;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.openbravo.base.exception.OBException;
import org.openbravo.client.kernel.BaseActionHandler;

import com.tripad.cootrack.utility.OpenApiUtils;
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
        HashMap<String,String> tempDataHash = new HashMap<String , String>();
        ArrayList<HashMap<String,String>> tempDataServer = new ArrayList<HashMap<String,String>>();
        try {
            OpenApiUtils utils = new OpenApiUtils();
            
            hasil = utils.requestStringListChildAccount().get("msg").toString();
            //debug code
            //hasil =utils.requestStringListMonitoring().toString()+" POPOP "+utils.requestStringListChildAccount().toString();
            JSONArray childList = (JSONArray) utils.requestStringListChildAccount().get("children");
            String rslt = "";
            OBCriteria<TmcListChildAcc> tmcListChildAcc = null;
            OBCriteria<TmcListChildAcc> tmcNotExsListChildAcc = null;
            for (int i = 0; i < childList.length(); i++) {
                String id = childList.getJSONObject(i).get("id").toString();
                String name = childList.getJSONObject(i).get("name").toString();
                String showname = childList.getJSONObject(i).get("showname").toString();
//                
//                tempDataHash.put("id", id);
//                tempDataHash.put("name", name);
//                tempDataHash.put("showname", showname);
//                
//                tempDataServer.add(tempDataHash);
               
                tmcListChildAcc = OBDal.getInstance()
                        .createCriteria(TmcListChildAcc.class);
                tmcListChildAcc.add(Restrictions.eq(TmcListChildAcc.PROPERTY_VALUE, id));
                
                if (tmcListChildAcc.count() == 0 ) { //bila tidak ada maka insert
                    TmcListChildAcc newTmcListChildAcc = OBProvider.getInstance().get(TmcListChildAcc.class);
                    
                    newTmcListChildAcc.setActive(true);
                    newTmcListChildAcc.setValue(id);
                    newTmcListChildAcc.setName(name);
                    newTmcListChildAcc.setShowname(showname);
                    
                    OBDal.getInstance().save(newTmcListChildAcc);
                    OBDal.getInstance().flush();
                } else { //bila adaa edit
                    
                    tmcListChildAcc.list().get(0).setName(name);
                    tmcListChildAcc.list().get(0).setShowname(showname);
                    
                    OBDal.getInstance().save(tmcListChildAcc.list().get(0));
                    OBDal.getInstance().flush();
                }
                
                
                // Bagian delete yg tidak ada di server
//                tmcNotExsListChildAcc = OBDal.getInstance()
//                        .createCriteria(TmcListChildAcc.class);
//                tmcNotExsListChildAcc.add(Restrictions.not(Restrictions.eq(TmcListChildAcc.PROPERTY_VALUE, id))); //
//                
//                //TmcListChildAcc notExistsTmcListChildAcc = ;
//                if (tmcNotExsListChildAcc.count() > 0) {
//                    OBDal.getInstance().remove(tmcNotExsListChildAcc.list().get(0));
//                    OBDal.getInstance().flush();
//                }
                
               
                
            }
            
            
            OBDal.getInstance().commitAndClose();
            
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