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
import com.tripad.cootrack.utility.CootrackHttpClient;
import com.tripad.cootrack.utility.OpenApiUtils;
import com.tripad.cootrack.utility.ResponseResultToDB;
import com.tripad.cootrack.utility.exception.CustomJsonErrorResponseException;

/**
 *
 * @author mfachmirizal
 */
public class RefreshListFilteredStatusCarByImei extends BaseActionHandler {
    static User COOTRACK_USER = OBContext.getOBContext().getUser();
    
    static CootrackHttpClient con = new CootrackHttpClient();
    static OpenApiUtils utils = new OpenApiUtils(con,COOTRACK_USER);
    
    @SuppressWarnings("finally")
    protected JSONObject execute(Map<String, Object> parameters, String data) {
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
                
                // loop dulu list imei
                OBCriteria<TmcCar> tmcCarCriteria = OBDal.getInstance().createCriteria(TmcCar.class);
                tmcCarCriteria.add(Restrictions.eq(TmcCar.PROPERTY_CREATEDBY, COOTRACK_USER));
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
                    JSONObject hasilRetrieve = utils.requestStatusFilteredCarByImei(wave.substring(1));
                    hasil = hasilRetrieve.get("msg").toString();
                    if (hasil.length() != 0) {
                        break;
                    } else {
                        new ResponseResultToDB(utils).validateCarStatusList(headerId, hasilRetrieve);
                    }
                    Thread.sleep(50);
                }

                json.put("jawaban", hasil);
            } else {
                json.put("jawaban", "This Openbravo user not synchronized with OpenAPI !");
            }
            // and return it
            return json;
        } catch (CustomJsonErrorResponseException jre) {
            System.out.println("MASUK JSONEXCEPTION");
            json.put("jawaban", jre.getMessage());
        } catch (JSONException e) {
            System.out.println("HASIL : EXCEPTION");
            json.put("jawaban", e.getMessage());
        } catch (Throwable t) {
            json.put("jawaban", "Internal Error : " + t.getMessage());
        } finally {
            con.shutdown();
            return json;
        }
        
    }
}
