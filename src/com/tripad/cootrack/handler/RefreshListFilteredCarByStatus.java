/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.exception.OBException;
import org.openbravo.client.kernel.BaseActionHandler;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.access.User;

import com.tripad.cootrack.data.TmcCar;
import com.tripad.cootrack.utility.OpenApiUtils;

/**
 *
 * @author mfachmirizal
 */
public class RefreshListFilteredCarByStatus extends BaseActionHandler {
  private User COOTRACK_USER = OBContext.getOBContext().getUser();

  protected JSONObject execute(Map<String, Object> parameters, String data) {
    String hasil = "";
    String imeiString = "";
    List<TmcCar> carList = null;
    List<String> waveList = new ArrayList<String>();
    OpenApiUtils utils = new OpenApiUtils();

    // hasil = (imeiList.size()) + "";
    try {
      // for () {
      // JSONObject hasilRetrieve = utils.requestStringListChildAccount(null);
      // }

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
          // requestData disini
          imeiString = "";
          count = 1;
          waveCount++;
        }
        count++;
      }

      int cc = 0;

      for (String wave : waveList) {
        String arr[] = utils.convertToArray(wave.substring(1), ",");
        /**
         * JSONObject hasilRetrieve = null; // nanti ini hapus, gunakan contoh yg bawah //
         * JSONObject hasilRetrieve = utils.requestStringListChildAccount(null); if
         * (hasilRetrieve.get("msg").toString().length() > 0) { hasil =
         * hasilRetrieve.get("msg").toString(); } else { // new
         * ResponseResultToDB().validateBPList(hasilRetrieve); } count = 0; } /*
         **/
        Thread.sleep(300);
        cc++;
      }

      // System.out.println("HASIL : " + hasil);
      // if (hasil.length() == 0) {
      // Retrieve seluruh Data dari OpenAPi Ke BP
      // new ResponseResultToDB().validateBPList(hasilRetrieve);

      // JSONArray dataList = (JSONArray) hasilRetrieve.get("children");

      // }
      // new CustomJsonErrorResponse("5555", "Fatal protocol violation :
      // "+e.getMessage()).getErrResponse();
      JSONObject json = new JSONObject();
      json.put("jawaban", hasil);

      // and return it
      return json;
    } catch (Exception e) {
      throw new OBException(e);
    } catch (Throwable ex) {
      throw new OBException(ex);
    }
  }
}
