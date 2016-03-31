/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tripad.cootrack.utility;

import com.tripad.cootrack.pojo.CustomerClass;
import com.tripad.cootrack.utility.exception.CustomJsonErrorResponseException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.exception.OBException;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mfachmirizal
 */
public class CollectCustomerFromDB {
    private String target;
    private OpenApiUtils utils ;
    final private static Logger log = LoggerFactory.getLogger(CollectCustomerFromDB.class);
    //parameterless
    public CollectCustomerFromDB() {
        utils= new OpenApiUtils();
    }
    
    //constructed with target
    public CollectCustomerFromDB(String targetExt) {
        target = targetExt;
    }
    
    //with utils parameter
    public CollectCustomerFromDB(OpenApiUtils extUtils) {
        utils= extUtils;
    }
    
    //constructed with target & utils
    public CollectCustomerFromDB(OpenApiUtils extUtils,String targetExt) {
        utils= extUtils;
        target = targetExt;
    }
    
    //with connection class
//    public CollectCustomerFromDB(CootrackHttpClient conParam) {
//        super(conParam);
//        
//    }
//    
//    //constructed with target
//    public CollectCustomerFromDB(String targetExt) {
//        super();
//        target = targetExt;
//    }
//    
//    //constructed with target and httpconn 
//    public CollectCustomerFromDB(CootrackHttpClient conParam,String targetExt) {
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
        //JSONObject hasilRetrieve = utils.requestListChildAccount(this.target);
        fillList(result,this.target,0);
        //utils.closeConnection();
        
        int i = 1;
        for (CustomerClass cc : result) {
            System.out.println("data ("+i+") : "+cc.getName());
//            int c = 1;
//            for (CustomerClass child : cc.getChildren()) {
//                System.out.println("child ("+c+") : "+cc.getName());
//                c++;
//            }
            i++;
        }
        return result;
    }
    
    
    
    /* mendapatkan daftar Customer dari Server secara Rekursif */ /* lvlnya belum beres coba benerin ntr */
    
    public void fillList(List<CustomerClass> result,String target,int lvl)
            throws Exception, OBException, JSONException, CustomJsonErrorResponseException, Throwable {
        CustomerClass customerClass;
        OBCriteria<Category> listcategoryCriteria = OBDal.getInstance().createCriteria(Category.class);
        listcategoryCriteria.add(Restrictions.eq(Category.PROPERTY_SEARCHKEY,target ));
        
        for (Category category :listcategoryCriteria.list() ) {
            for (BusinessPartner customer : category.getBusinessPartnerList()) {
                customerClass = new CustomerClass();
                //log.error("nama : "+customer.getSearchKey());
                customerClass.setId(customer.getId());
                customerClass.setName(customer.getSearchKey());
                customerClass.setShowname(customer.getName());
                //if (lvl == 0) {
                    result.add(customerClass);
                //} else {
                 
               // }
        
//                List<CustomerClass> customerChild = new ArrayList<CustomerClass>();
//                OBCriteria<Category> listChildCriteria = OBDal.getInstance().createCriteria(Category.class);
//                listChildCriteria.add(Restrictions.eq(Category.PROPERTY_SEARCHKEY,customer.getSearchKey() ));
//                if (listChildCriteria.count() > 0) {
                fillList(result,customer.getSearchKey(),lvl);
                
                
//                }
//                customerClass.setChildren(customerChild);
//
//                
//                result.add(customerClass);
            }
        }
        //return null;
    }
//    public List<CustomerClass> fillList(String target,int lvl)
//            throws Exception, OBException, JSONException, CustomJsonErrorResponseException, Throwable {
//        
//        List<CustomerClass> result = new ArrayList<CustomerClass>();
//        CustomerClass customerClass;
//
////        JSONArray childList = (JSONArray) hasilRetrieve.get("children");
//        
//
//        OBCriteria<Category> listcategoryCriteria = OBDal.getInstance().createCriteria(Category.class);
//        listcategoryCriteria.add(Restrictions.eq(Category.PROPERTY_SEARCHKEY,target ));
//        
//        for (Category category :listcategoryCriteria.list() ) {
//            for (BusinessPartner customer : category.getBusinessPartnerList()) {
//                customerClass = new CustomerClass();
//                log.error("nama : "+customer.getSearchKey());
//                customerClass.setId(customer.getId());
//                customerClass.setName(customer.getSearchKey());
//                customerClass.setShowname(customer.getName());
////                
//                List<CustomerClass> customerChild = new ArrayList<CustomerClass>();
//                OBCriteria<Category> listChildCriteria = OBDal.getInstance().createCriteria(Category.class);
//                listChildCriteria.add(Restrictions.eq(Category.PROPERTY_SEARCHKEY,customer.getSearchKey() ));
//                if (listChildCriteria.count() > 0) {
//                   customerChild = fillList(customer.getSearchKey(),1);
//                }
//                customerClass.setChildren(customerChild);
//
//                
//                result.add(customerClass);
//            }
//        }
//        
//        return result;
//    }
}
