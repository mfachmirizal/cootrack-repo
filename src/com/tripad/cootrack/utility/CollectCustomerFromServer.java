/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tripad.cootrack.utility;

import com.tripad.cootrack.data.TmcCar;
import com.tripad.cootrack.data.TmcDocumentUpdateLine;
import com.tripad.cootrack.pojo.CustomerClass;
import static com.tripad.cootrack.utility.ResponseResultToDB.ISBYVALUE;
import com.tripad.cootrack.utility.exception.CustomJsonErrorResponseException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.exception.OBException;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.access.User;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Category;

/**
 *
 * @author mfachmirizal
 */
public class CollectCustomerFromServer {
    private String target;
    private OpenApiUtils utils ;
    
    //parameterless
    public CollectCustomerFromServer() {
        utils= new OpenApiUtils();
    }
    
    //constructed with target
    public CollectCustomerFromServer(String targetExt) {
        target = targetExt;
    }
    
    //with utils parameter
    public CollectCustomerFromServer(OpenApiUtils extUtils) {
        utils= extUtils;
    }
    
    //constructed with target & utils
    public CollectCustomerFromServer(OpenApiUtils extUtils,String targetExt) {
        utils= extUtils;
        target = targetExt;
    }
    
    //with connection class
//    public CollectCustomerFromServer(CootrackHttpClient conParam) {
//        super(conParam);
//        
//    }
//    
//    //constructed with target
//    public CollectCustomerFromServer(String targetExt) {
//        super();
//        target = targetExt;
//    }
//    
//    //constructed with target and httpconn 
//    public CollectCustomerFromServer(CootrackHttpClient conParam,String targetExt) {
//        super(conParam);
//        target = targetExt;
//    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }
    
    // method
    
    public List<CustomerClass> getList() throws Exception, OBException, JSONException, CustomJsonErrorResponseException, Throwable{
        List<CustomerClass> result = new ArrayList<>();
        JSONObject hasilRetrieve = utils.requestListChildAccount(this.target);
        fillList(result,hasilRetrieve);
        //utils.closeConnection();
        
        System.out.println("yang pertama: "+result.get(0).getName());
        return result;
    }
    
    /* mendapatkan daftar Customer dari Server secara Rekursif */
    public void fillList(List<CustomerClass> result,JSONObject hasilRetrieve)
            throws Exception, OBException, JSONException, CustomJsonErrorResponseException, Throwable {
        CustomerClass customerClass = new CustomerClass();

        JSONArray childList = (JSONArray) hasilRetrieve.get("children");

        JSONObject hasilTargetChild;
        
        for (int i = 0; i < childList.length(); i++) {
            String id = childList.getJSONObject(i).get("id").toString();
            String name = childList.getJSONObject(i).get("name").toString();
            String showname = childList.getJSONObject(i).get("showname").toString();
            
            customerClass.setId(id);
            customerClass.setName(name);
            customerClass.setShowname(showname);
            
            
            hasilTargetChild = utils.requestListChildAccount(name);
            
            List<CustomerClass> customerChild = new ArrayList<CustomerClass>();
            // cek apakah dia punya anak lagi ?
            try {
                JSONArray childListJSONArray = (JSONArray) hasilTargetChild.get("children");
                
                if (childListJSONArray.length() > 0) {
                    fillList(customerChild,hasilTargetChild);
                }
            } catch (JSONException jex) {
                System.out.println("Jex1 : " + hasilTargetChild.toString());
                //skip, berarti tidak punya anak
                System.out.println("Jex1 : " + jex.getMessage());
            }
            
            customerClass.setChildren(customerChild);
            
            result.add(customerClass);

        } //end loop utama
        System.out.println("End akhir : Collect Customer From Server");        
    }
    
    
}
