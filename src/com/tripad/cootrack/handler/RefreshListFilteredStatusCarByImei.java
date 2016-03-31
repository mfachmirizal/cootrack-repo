/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.client.kernel.BaseActionHandler;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.access.User;

import com.tripad.cootrack.data.TmcCar;
import com.tripad.cootrack.pojo.CustomerClass;
import com.tripad.cootrack.utility.CollectCustomerFromDB;
import com.tripad.cootrack.utility.CollectCustomerFromServer;
import com.tripad.cootrack.utility.CootrackHttpClient;
import com.tripad.cootrack.utility.OpenApiUtils;
import com.tripad.cootrack.utility.ResponseResultToDB;
import com.tripad.cootrack.utility.exception.CustomJsonErrorResponseException;
import org.openbravo.model.common.businesspartner.BusinessPartner;

/**
 *
 * @author mfachmirizal
 */
public class RefreshListFilteredStatusCarByImei extends BaseActionHandler {
    static boolean PROCESS_BY_ALL_IMEI = true;
    static boolean PROCESS_BY_TARGET_GO_TO_ELSE = false;
    
    
    @SuppressWarnings("finally")
    protected JSONObject execute(Map<String, Object> parameters, String data) {
        User COOTRACK_USER = OBContext.getOBContext().getUser();
        System.out.println("RefreshListFilteredStatusCarByImei : "+COOTRACK_USER.getUsername());
        //CootrackHttpClient con = new CootrackHttpClient();
        
        OpenApiUtils utils = new OpenApiUtils(/*, COOTRACK_USER*/);
        JSONObject json = new JSONObject();
        String headerId = "";
        String hasil = "";
        String imeiString = "";
        List<TmcCar> carList = null;
        List<String> waveList = new ArrayList<String>();
        try {
            if (utils.getCurrentPassword() != null) {
                //parameter
                final JSONObject jsonData = new JSONObject(data);
                final JSONArray headerIds = jsonData.getJSONArray("headers");
                
                for (int i = 0; i < headerIds.length(); i++) {
                    headerId = headerIds.getString(i);
                }
                
                if (PROCESS_BY_TARGET_GO_TO_ELSE) {
                    // loop dulu list imei
                    OBCriteria<TmcCar> tmcCarCriteria = OBDal.getInstance().createCriteria(TmcCar.class);
                    //ini tanpa filter sekarang tmcCarCriteria.add(Restrictions.eq(TmcCar.PROPERTY_CREATEDBY, COOTRACK_USER));
                    int count = 1;
                    int waveCount = 0;
                    for (TmcCar tmcCar : tmcCarCriteria.list()) {
                        imeiString += ("," + tmcCar.getImei());
                        // imeiString = imeiString.substring(1);
                        try {
                            waveList.set(waveCount, imeiString);
                        } catch (IndexOutOfBoundsException iox) {
                            waveList.add(waveCount, imeiString);
                        }
                        
                        if (count % 99 == 0) {
                            imeiString = "";
                            count = 0; //1
                            waveCount++;
                        }
                        count++;
                    }
                    
                    for (String wave : waveList) {
                        //String arr[] = utils.convertToArray(wave.substring(1), ",");
                        // requestData disini
                        JSONObject hasilRetrieve = utils.requestStatusFilteredCarByImei("byimei",wave.substring(1));
                        hasil = hasilRetrieve.get("msg").toString();
                        if (hasil.length() != 0) {
                            break;
                        } else {
                            new ResponseResultToDB(utils).validateCarStatusList(headerId, hasilRetrieve);
                        }
                        Thread.sleep(50);
                    }
                } else { //by target
                    //List<CustomerClass> listCustomer = new CollectCustomerFromServer(utils,COOTRACK_USER.getUsername()).getList();
                    List<CustomerClass> listCustomer = new CollectCustomerFromDB(utils,COOTRACK_USER.getUsername()).getList();
                    List<String> customerValueList = new ArrayList<String>();
                    customerValueList.add(COOTRACK_USER.getUsername());
                    for (CustomerClass customerClass : listCustomer) {
                        customerValueList.add(customerClass.getName());
                    }
                    
                    OBCriteria<BusinessPartner> bPartnerCrit = OBDal.getInstance().createCriteria(BusinessPartner.class);
                    bPartnerCrit.add(Restrictions.in(BusinessPartner.PROPERTY_SEARCHKEY, customerValueList));
                    
                    int count = 1;
                    int waveCount = 0;
                    for (BusinessPartner bp : bPartnerCrit.list()) {
                        for (TmcCar tmcCar : bp.getTmcCarList()) {
                            imeiString += ("," + tmcCar.getImei());
                            
                            try {
                                waveList.set(waveCount, imeiString);
                            } catch (IndexOutOfBoundsException iox) {
                                waveList.add(waveCount, imeiString);
                            }
                            
                            if (count % 99 == 0) {
                                imeiString = "";
                                count = 0; //1
                                waveCount++;
                            }
                            count++;
                        }
                    }
                    
                    for (String wave : waveList) {
                        //String arr[] = utils.convertToArray(wave.substring(1), ",");
                        // requestData disini
                        JSONObject hasilRetrieve = utils.requestStatusFilteredCarByImei("byimei",wave.substring(1));
                        hasil = hasilRetrieve.get("msg").toString();
                        if (hasil.length() != 0) {
                            break;
                        } else {
                            new ResponseResultToDB(utils).validateCarStatusList(headerId, hasilRetrieve);
                        }
                        Thread.sleep(25);
                    }
                    //ikutin dengan metode wave list ntr
                    
//
//                    JSONObject hasilRetrieve = utils.requestStatusFilteredCarByImei("bytarget",COOTRACK_USER.getUsername());
//                    hasil = hasilRetrieve.get("msg").toString();
//                    if (hasil.length() == 0) {
//                        // Hasil retrieve simpan ke OB
//                        new ResponseResultToDB(utils).validateCarStatusList(headerId, hasilRetrieve);
//                    }
                } //end by target
                
                json.put("jawaban", hasil);
                //Historical Test
                OBDal.getInstance().flush();
                OBDal.getInstance().commitAndClose();
                
            } else {
                json.put("jawaban", "This Openbravo user not synchronized with OpenAPI !");
            }
            // and return it
            return json;
        } catch (CustomJsonErrorResponseException jre) {
            OBDal.getInstance().rollbackAndClose();
            System.out.println("MASUK JSONEXCEPTION");
            json.put("jawaban", jre.getMessage());
        } catch (JSONException e) {
            OBDal.getInstance().rollbackAndClose();
            System.out.println("HASIL : EXCEPTION");
            json.put("jawaban", e.getMessage());
        } catch (Throwable t) {
            OBDal.getInstance().rollbackAndClose();
            json.put("jawaban", "Internal Error : " + t.getMessage());
        } finally {
            utils.closeConnection();
            return json;
        }
        
    }
}
