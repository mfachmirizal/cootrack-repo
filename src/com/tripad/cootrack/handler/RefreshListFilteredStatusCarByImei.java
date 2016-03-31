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
	static boolean PROCESS_BY_IMEI = true;
	static boolean PROCESS_BY_TARGET = false;
  

  @SuppressWarnings("finally")
  protected JSONObject execute(Map<String, Object> parameters, String data) {
    User COOTRACK_USER = OBContext.getOBContext().getUser();
      System.out.println("RefreshListFilteredStatusCarByImei : "+COOTRACK_USER.getUsername());
    CootrackHttpClient con = new CootrackHttpClient();  
    OpenApiUtils utils = new OpenApiUtils(con/*, COOTRACK_USER*/);
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

		if (PROCESS_BY_TARGET) {
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
	  } else {
		  JSONObject hasilRetrieve = utils.requestStatusFilteredCarByImei("bytarget",COOTRACK_USER.getUsername());
		  hasil = hasilRetrieve.get("msg").toString();
		  if (hasil.length() == 0) {
          // Hasil retrieve simpan ke OB
			new ResponseResultToDB(utils).validateCarStatusList(headerId, hasilRetrieve);
		  }
	  }

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
      con.shutdown();
      return json;
    }

  }
}
