/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.utility;

import com.tripad.cootrack.data.TmcCar;
import com.tripad.cootrack.data.TmcListChildAcc;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.exception.OBException;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Category;

/**
 *
 * @author mfachmirizal
 */
public class ResponseResultToDB {
    
    public ResponseResultToDB() {
    }
    
    public void validateChildList(JSONObject hasilRetrieve) throws Exception ,OBException{
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
        
        //adegan menghapus record yg ada di local tapi tidak ada di server open api
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
    
    
    public void validateBPList(JSONObject hasilRetrieve) throws Exception ,OBException{
        ArrayList<String> tempIdDataServer = new ArrayList<String>();
        JSONArray childList = (JSONArray) hasilRetrieve.get("children");
        OpenApiUtils utils = new OpenApiUtils();
        String rslt = "";
        OBCriteria<BusinessPartner> tmcListChildAcc = null;
        //OBCriteria<TmcListChildAcc> tmcNotExsListChildAcc = null;
        //var mobil
        JSONObject hasilTarget;
        JSONArray carList;
        for (int i = 0; i < childList.length(); i++) {
            String id = childList.getJSONObject(i).get("id").toString();
            String name = childList.getJSONObject(i).get("name").toString();
            String showname = childList.getJSONObject(i).get("showname").toString();
            
            tmcListChildAcc = OBDal.getInstance()
                    .createCriteria(BusinessPartner.class);
            tmcListChildAcc.add(Restrictions.eq(BusinessPartner.PROPERTY_TMCOPENAPIIDENT, id));
            
            if (tmcListChildAcc.count() == 0 ) { //bila tidak ada maka insert
                BusinessPartner newBusinessPartner = OBProvider.getInstance().get(BusinessPartner.class);
                
                newBusinessPartner.setActive(true);
                newBusinessPartner.setTmcOpenapiIdent(id);
                //    newBusinessPartner.setTmcOpenapiUser(name);
                newBusinessPartner.setSearchKey(name);
                newBusinessPartner.setName(showname);
                newBusinessPartner.setConsumptionDays(Long.getLong("0"));
                newBusinessPartner.setCreditLimit(BigDecimal.ZERO);
                
                OBCriteria<Category> bpCrit = OBDal.getInstance()
                        .createCriteria(Category.class);
                bpCrit.add(Restrictions.eq(Category.PROPERTY_SEARCHKEY, "CustomerOpenApi"));
                
                newBusinessPartner.setBusinessPartnerCategory(bpCrit.list().get(0));
                
                OBDal.getInstance().save(newBusinessPartner);
                OBDal.getInstance().flush();
                
                //get car list
                hasilTarget = utils.requestStringListChildAccount(name);
               // try {
                    carList = (JSONArray) hasilTarget.get("data");
                    //loop car
                    for (int c = 0; c < carList.length(); c++) {
                        TmcCar newTmcCar = OBProvider.getInstance().get(TmcCar.class);
                        newTmcCar.setActive(true);
                        newTmcCar.setBpartner(newBusinessPartner);
                        newTmcCar.setImei(carList.getJSONObject(c).get("imei").toString());
                        newTmcCar.setName(carList.getJSONObject(c).get("name").toString());
                        newTmcCar.setPlateNo(carList.getJSONObject(c).get("number").toString());
                        newTmcCar.setTelephone(carList.getJSONObject(c).get("phone").toString());
                        newTmcCar.setTime(Long.valueOf(carList.getJSONObject(c).get("in_time").toString()));
                        newTmcCar.setOUTTime(Long.valueOf(carList.getJSONObject(c).get("out_time").toString()));
                        
                        OBDal.getInstance().save(newTmcCar);
                        OBDal.getInstance().flush();
                        //newTmcCar.set
                    }
             //   }catch (JSONException jex) {
                    //do nothing, data tidak ada
             //   }
                
            } else { //bila adaa edit
                
                //   tmcListChildAcc.list().get(0).setTmcOpenapiUser(name);
                tmcListChildAcc.list().get(0).setSearchKey(name);
                tmcListChildAcc.list().get(0).setName(showname);
                
                OBDal.getInstance().save(tmcListChildAcc.list().get(0));
                OBDal.getInstance().flush();
                
                //edit line
                hasilTarget = utils.requestStringListChildAccount(name);
   //             try {
                    carList = (JSONArray) hasilTarget.get("data");
                    
                    for (int c = 0; c < carList.length(); c++) {
                        //carList.getJSONObject(i).get("imei").toString()
                        OBCriteria<TmcCar> tmcListCar = OBDal.getInstance().createCriteria(TmcCar.class);
                        tmcListCar.add(Restrictions.eq(TmcCar.PROPERTY_BPARTNER, tmcListChildAcc.list().get(0)));
                        tmcListCar.add(Restrictions.eq(TmcCar.PROPERTY_IMEI, carList.getJSONObject(i).get("imei").toString()));
                        
                        
                        tmcListCar.list().get(0).setImei(carList.getJSONObject(c).get("imei").toString());
                        tmcListCar.list().get(0).setName(carList.getJSONObject(c).get("name").toString());
                        tmcListCar.list().get(0).setPlateNo(carList.getJSONObject(c).get("number").toString());
                        tmcListCar.list().get(0).setTelephone(carList.getJSONObject(c).get("phone").toString());
                        tmcListCar.list().get(0).setTime(Long.valueOf(carList.getJSONObject(c).get("in_time").toString()));
                        tmcListCar.list().get(0).setOUTTime(Long.valueOf(carList.getJSONObject(c).get("out_time").toString()));
                        
                        OBDal.getInstance().save(tmcListCar.list().get(0));
                        OBDal.getInstance().flush();
                    }
        //        }catch (JSONException jex) {
                    //do nothing, data tidak ada
        //        }
                
                
            }
            
            tempIdDataServer.add(id);
            
        }
        
        //adegan menghapus record yg ada di local tapi tidak ada di server open api ||Jangan LUPA DELETE LINENYA DULU
//        tmcListChildAcc = OBDal.getInstance()
//                .createCriteria(BusinessPartner.class);
//        tmcListChildAcc.add(Restrictions.not(Restrictions.in(BusinessPartner.PROPERTY_TMCOPENAPIIDENT, tempIdDataServer))); //
//
        
        //       throw new OBException(tmcListChildAcc.count()+"");
//        for (BusinessPartner removeRecord : tmcListChildAcc.list()) {
//            OBDal.getInstance().remove(removeRecord);
//            OBDal.getInstance().flush();
//        }
        
        
        OBDal.getInstance().commitAndClose();
    }
}
