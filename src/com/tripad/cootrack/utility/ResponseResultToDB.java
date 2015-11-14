/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.utility;

import com.tripad.cootrack.data.TmcListChildAcc;
import java.util.ArrayList;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;

/**
 *
 * @author mfachmirizal
 */
public class ResponseResultToDB {
    
    public ResponseResultToDB() {
    }
    
    public void validateChildList(JSONObject hasilRetrieve) throws Exception {
        ArrayList<String> tempIdDataServer = new ArrayList<String>();
        JSONArray childList = (JSONArray) hasilRetrieve.get("children");
        String rslt = "";
        OBCriteria<TmcListChildAcc> tmcListChildAcc = null;
        //OBCriteria<TmcListChildAcc> tmcNotExsListChildAcc = null;
        for (int i = 0; i < childList.length(); i++) {
            String id = childList.getJSONObject(i).get("id").toString();
            String name = childList.getJSONObject(i).get("name").toString();
            String showname = childList.getJSONObject(i).get("showname").toString();
            
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
            
            tempIdDataServer.add(id);
            
        }
        
        tmcListChildAcc = OBDal.getInstance()
                .createCriteria(TmcListChildAcc.class);
        tmcListChildAcc.add(Restrictions.not(Restrictions.in(TmcListChildAcc.PROPERTY_VALUE, tempIdDataServer))); //
        //TmcListChildAcc notExistsTmcListChildAcc = ;
        for (TmcListChildAcc removeRecord : tmcListChildAcc.list()) {
            OBDal.getInstance().remove(removeRecord);
            OBDal.getInstance().flush();
        }
        
        
        OBDal.getInstance().commitAndClose();
    }
}
