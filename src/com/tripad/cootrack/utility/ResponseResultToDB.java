/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.utility;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.exception.OBException;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.access.User;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Category;

import com.tripad.cootrack.data.TmcCar;
import com.tripad.cootrack.data.TmcListChildAcc;

/**
 *
 * @author mfachmirizal
 */
public class ResponseResultToDB {
  private User COOTRACK_USER = OBContext.getOBContext().getUser();

  public ResponseResultToDB() {
  }

  public void validateChildList(JSONObject hasilRetrieve) throws Exception, OBException {
    ArrayList<String> tempIdDataServer = new ArrayList<String>();
    JSONArray childList = (JSONArray) hasilRetrieve.get("children");

    String rslt = "";
    OBCriteria<TmcListChildAcc> tmcListChildAcc = null;
    // OBCriteria<TmcListChildAcc> tmcNotExsListChildAcc = null;
    for (int i = 0; i < childList.length(); i++) {
      String id = childList.getJSONObject(i).get("id").toString();
      String name = childList.getJSONObject(i).get("name").toString();
      String showname = childList.getJSONObject(i).get("showname").toString();

      tmcListChildAcc = OBDal.getInstance().createCriteria(TmcListChildAcc.class);
      tmcListChildAcc.add(Restrictions.eq(TmcListChildAcc.PROPERTY_VALUE, id));
      tmcListChildAcc.add(Restrictions.eq(TmcListChildAcc.PROPERTY_CREATEDBY, COOTRACK_USER));

      if (tmcListChildAcc.count() == 0) { // bila tidak ada maka insert
        TmcListChildAcc newTmcListChildAcc = OBProvider.getInstance().get(TmcListChildAcc.class);

        newTmcListChildAcc.setActive(true);
        newTmcListChildAcc.setValue(id);
        newTmcListChildAcc.setName(name);
        newTmcListChildAcc.setShowname(showname);

        OBDal.getInstance().save(newTmcListChildAcc);
        OBDal.getInstance().flush();

      } else { // bila adaa edit

        tmcListChildAcc.list().get(0).setName(name);
        tmcListChildAcc.list().get(0).setShowname(showname);

        OBDal.getInstance().save(tmcListChildAcc.list().get(0));
        OBDal.getInstance().flush();
      }

      tempIdDataServer.add(id);

    }

    // adegan menghapus record yg ada di local tapi tidak ada di server open api
    tmcListChildAcc = OBDal.getInstance().createCriteria(TmcListChildAcc.class);
    tmcListChildAcc
        .add(Restrictions.not(Restrictions.in(TmcListChildAcc.PROPERTY_VALUE, tempIdDataServer))); //
    tmcListChildAcc.add(Restrictions.eq(TmcListChildAcc.PROPERTY_CREATEDBY, COOTRACK_USER));
    // TmcListChildAcc notExistsTmcListChildAcc = ;
    for (TmcListChildAcc removeRecord : tmcListChildAcc.list()) {
      OBDal.getInstance().remove(removeRecord);
      OBDal.getInstance().flush();
    }

    OBDal.getInstance().commitAndClose();
  }

  public void validateBPList(JSONObject hasilRetrieve) throws Exception, OBException {
    ArrayList<String> tempIdDataServer = new ArrayList<String>();
    ArrayList<String> tempImeiServer = new ArrayList<String>();
    JSONArray childList = (JSONArray) hasilRetrieve.get("children");
    OpenApiUtils utils = new OpenApiUtils();
    String rslt = "";
    OBCriteria<BusinessPartner> tmcListChildAcc = null;
    // OBCriteria<TmcListChildAcc> tmcNotExsListChildAcc = null;
    // var mobil
    JSONObject hasilTarget;
    JSONArray carList;
    for (int i = 0; i < childList.length(); i++) {
      String id = childList.getJSONObject(i).get("id").toString();
      String name = childList.getJSONObject(i).get("name").toString();
      String showname = childList.getJSONObject(i).get("showname").toString();

      tmcListChildAcc = OBDal.getInstance().createCriteria(BusinessPartner.class);
      tmcListChildAcc.add(Restrictions.eq(BusinessPartner.PROPERTY_TMCOPENAPIIDENT, id));
      tmcListChildAcc.add(Restrictions.eq(BusinessPartner.PROPERTY_CREATEDBY, COOTRACK_USER));

      if (tmcListChildAcc.list().isEmpty()) { // bila tidak ada maka insert
        BusinessPartner newBusinessPartner = OBProvider.getInstance().get(BusinessPartner.class);

        newBusinessPartner.setActive(true);
        newBusinessPartner.setTmcOpenapiIdent(id);
        // newBusinessPartner.setTmcOpenapiUser(name);
        newBusinessPartner.setSearchKey(name);
        newBusinessPartner.setName(showname);
        newBusinessPartner.setConsumptionDays(Long.getLong("0"));
        newBusinessPartner.setCreditLimit(BigDecimal.ZERO);

        OBCriteria<Category> bpCrit = OBDal.getInstance().createCriteria(Category.class);
        bpCrit.add(Restrictions.eq(Category.PROPERTY_SEARCHKEY, "CustomerOpenApi"));
        if (bpCrit.list().isEmpty()) {
          Category newBpCat = OBProvider.getInstance().get(Category.class);
          newBpCat.setActive(true);
          newBpCat.setName("CustomerOpenApi");
          newBpCat.setSearchKey("CustomerOpenApi");
          newBpCat.setDescription("Business Partner Category For Customer Open Api");

          OBDal.getInstance().save(newBpCat);
          OBDal.getInstance().flush();

          newBusinessPartner.setBusinessPartnerCategory(newBpCat);
        } else {
          newBusinessPartner.setBusinessPartnerCategory(bpCrit.list().get(0));
        }

        OBDal.getInstance().save(newBusinessPartner);
        OBDal.getInstance().flush();

        // get car list
        hasilTarget = utils.requestListChildAccount(name);
        try {
          // System.out.println(hasilTarget.toString());
          carList = (JSONArray) hasilTarget.get("data");
          // loop car
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
            // newTmcCar.set
          }
        } catch (JSONException jex) {
          // do nothing, data tidak ada
          // System.out.println("excpt> "+hasilTarget.toString());
        }

      } else { // bila adaa edit

        // tmcListChildAcc.list().get(0).setTmcOpenapiUser(name);

        tmcListChildAcc.list().get(0).setSearchKey(name);
        tmcListChildAcc.list().get(0).setName(showname);

        OBDal.getInstance().save(tmcListChildAcc.list().get(0));
        OBDal.getInstance().flush();

        // edit line
        hasilTarget = utils.requestListChildAccount(name);
        // try {

        carList = (JSONArray) hasilTarget.get("data");
        // System.out.println("masuk sini test 4: "+carList.toString());
        for (int c = 0; c < carList.length(); c++) {
          // carList.getJSONObject(i).get("imei").toString()
          OBCriteria<TmcCar> tmcListCar = OBDal.getInstance().createCriteria(TmcCar.class);
          tmcListCar.add(Restrictions.eq(TmcCar.PROPERTY_BPARTNER, tmcListChildAcc.list().get(0)));
          tmcListCar.add(Restrictions.eq(TmcCar.PROPERTY_CREATEDBY, COOTRACK_USER));
          tmcListCar.add(Restrictions.eq(TmcCar.PROPERTY_IMEI,
              carList.getJSONObject(c).get("imei").toString()));

          if (tmcListCar.list().isEmpty()) {
            TmcCar newTmcCar = OBProvider.getInstance().get(TmcCar.class);
            newTmcCar.setActive(true);
            newTmcCar.setBpartner(tmcListChildAcc.list().get(0));
            newTmcCar.setImei(carList.getJSONObject(c).get("imei").toString());
            newTmcCar.setName(carList.getJSONObject(c).get("name").toString());
            newTmcCar.setPlateNo(carList.getJSONObject(c).get("number").toString());
            newTmcCar.setTelephone(carList.getJSONObject(c).get("phone").toString());
            newTmcCar.setTime(Long.valueOf(carList.getJSONObject(c).get("in_time").toString()));
            newTmcCar.setOUTTime(Long.valueOf(carList.getJSONObject(c).get("out_time").toString()));

            OBDal.getInstance().save(newTmcCar);
            OBDal.getInstance().flush();
          } else {
            tmcListCar.list().get(0).setImei(carList.getJSONObject(c).get("imei").toString());
            tmcListCar.list().get(0).setName(carList.getJSONObject(c).get("name").toString());
            tmcListCar.list().get(0).setPlateNo(carList.getJSONObject(c).get("number").toString());
            tmcListCar.list().get(0).setTelephone(carList.getJSONObject(c).get("phone").toString());
            tmcListCar.list().get(0)
                .setTime(Long.valueOf(carList.getJSONObject(c).get("in_time").toString()));
            tmcListCar.list().get(0)
                .setOUTTime(Long.valueOf(carList.getJSONObject(c).get("out_time").toString()));

            OBDal.getInstance().save(tmcListCar.list().get(0));
            OBDal.getInstance().flush();
          }

          tempImeiServer.add(carList.getJSONObject(c).get("imei").toString());

        }

        // adegan menghapus LINE Bila tidak ada yg seragam
        OBCriteria<TmcCar> tmcListCarRemove = OBDal.getInstance().createCriteria(TmcCar.class);
        tmcListCarRemove
            .add(Restrictions.eq(TmcCar.PROPERTY_BPARTNER, tmcListChildAcc.list().get(0)));
        tmcListCarRemove.add(Restrictions.eq(TmcCar.PROPERTY_CREATEDBY, COOTRACK_USER));
        tmcListCarRemove
            .add(Restrictions.not(Restrictions.in(TmcCar.PROPERTY_IMEI, tempImeiServer)));

        for (TmcCar removeRecord : tmcListCarRemove.list()) {
          OBDal.getInstance().remove(removeRecord);
          OBDal.getInstance().flush();
        }

      }

      tempIdDataServer.add(id);

    }

    // adegan menghapus record yg ada di local tapi tidak ada di server open api ||Jangan LUPA
    // DELETE LINENYA DULU
    // header
    tmcListChildAcc = OBDal.getInstance().createCriteria(BusinessPartner.class);
    tmcListChildAcc.add(Restrictions
        .not(Restrictions.in(BusinessPartner.PROPERTY_TMCOPENAPIIDENT, tempIdDataServer))); //
    tmcListChildAcc.add(Restrictions.eq(BusinessPartner.PROPERTY_CREATEDBY, COOTRACK_USER));
    tmcListChildAcc.add(Restrictions.eq(BusinessPartner.PROPERTY_ACTIVE, true));
    OBCriteria<Category> bpCrit = OBDal.getInstance().createCriteria(Category.class);
    bpCrit.add(Restrictions.eq(Category.PROPERTY_SEARCHKEY, "CustomerOpenApi"));

    tmcListChildAcc.add(
        Restrictions.eq(BusinessPartner.PROPERTY_BUSINESSPARTNERCATEGORY, bpCrit.list().get(0)));

    for (BusinessPartner removeRecord : tmcListChildAcc.list()) {

      for (TmcCar removeLine : removeRecord.getTmcCarList()) {
        OBDal.getInstance().remove(removeLine);
        OBDal.getInstance().flush();
      }

      OBDal.getInstance().remove(removeRecord);
      OBDal.getInstance().flush();
    }

    OBDal.getInstance().commitAndClose();
  }
}
