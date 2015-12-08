/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Query;
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
import com.tripad.cootrack.data.TmcDocumentUpdate;
import com.tripad.cootrack.data.TmcDocumentUpdateLine;
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
        validateBPList(hasilRetrieve,null,null);
    }
    public void validateBPList(JSONObject hasilRetrieve,ArrayList<String> tempIdDataServerParam,ArrayList<String> tempImeiServerParam) throws Exception, OBException {
      System.out.println("terpanggil");
      ArrayList<String> tempIdDataServer = null;
      ArrayList<String> tempImeiServer = null;
      
      //cek apakah hasil pemanggilan rekursif?, bila iya maka set variable arraylist berdasarkan nilai sebelumnya
      if (tempIdDataServerParam == null) {
          tempIdDataServer = new ArrayList<String>();
      }else {
          tempIdDataServer = tempIdDataServerParam;
      }
      
      if (tempImeiServerParam == null) {
          tempImeiServer = new ArrayList<String>();
      }else {
          tempImeiServer = tempImeiServerParam;
      }
      
      
      ArrayList<JSONObject> tempChildJsonObject = new ArrayList<JSONObject>();
     // JSONArray childList = null;
      
      System.out.println("Awal : "+hasilRetrieve.toString());
      
     // try {
       JSONArray childList = (JSONArray) hasilRetrieve.get("children");
     // } catch(JSONException jex) {
    //    return;
    //  }
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
              
          } else { // bila BP SUdah adaa, maka edit
              
              // tmcListChildAcc.list().get(0).setTmcOpenapiUser(name);
              
              tmcListChildAcc.list().get(0).setSearchKey(name);
              tmcListChildAcc.list().get(0).setName(showname);
              
              OBDal.getInstance().save(tmcListChildAcc.list().get(0));
              OBDal.getInstance().flush();
              
              // edit line
              hasilTarget = utils.requestListChildAccount(name);
              // try {
              
              carList = (JSONArray) hasilTarget.get("data");
              
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
                  for (TmcDocumentUpdateLine tmcListChildCar : removeRecord.getTmcDocumentUpdateLineList()) {
                      OBDal.getInstance().remove(tmcListChildCar);
                      OBDal.getInstance().flush();
                  }
                  OBDal.getInstance().remove(removeRecord);
                  OBDal.getInstance().flush();
              }
              
          }
          
          tempIdDataServer.add(id);

          
          // cek apakah dia punya anak lagi ?          
          try {
            JSONArray childListChild = (JSONArray) hasilTarget.get("children");
            if (childListChild.length() > 0) {
              //validateBPList(hasilTarget);s
              System.out.println("ANAK : "+hasilTarget.toString());
              tempChildJsonObject.add(hasilTarget);
            }
          } catch (JSONException jex) {
            //skip, berarti tidak punya anak
            System.out.println("Jex : "+jex.getMessage());
          } catch (Throwable t) {
            //skip, berarti tidak punya anak
            System.out.println("Err : "+t.getMessage());
          }
          
          //Though, mungkin setiap JSONObject (hasilTarget) yg memiliki children, di simpan dulu
          //di ArrayList, lalu di eksekusi setelah tahap adegan menghapus record
          
      }//end loop utama
     
      System.out.println("End akhir : ");
      
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
              for (TmcDocumentUpdateLine tmcListChildCar : removeLine.getTmcDocumentUpdateLineList()) {
                  OBDal.getInstance().remove(tmcListChildCar);
                  OBDal.getInstance().flush();
              }
              OBDal.getInstance().remove(removeLine);
              OBDal.getInstance().flush();
          }
          OBDal.getInstance().remove(removeRecord);
          OBDal.getInstance().flush();
      }
      
      //get child from child
      try {
        //JSONArray childListChild = (JSONArray) hasilTarget.get("children");
          System.out.println("tempChildJsonObject : "+tempChildJsonObject.size());
        for (JSONObject jObject : tempChildJsonObject) {
          System.out.println("hasilTarget : "+jObject.toString());
          validateBPList(jObject,tempIdDataServer,tempImeiServer);
        }
      } catch (JSONException jex) {
        //skip, berarti tidak punya anak
        System.out.println("Jex : "+jex.getMessage());  /* */
      } catch (Throwable t) {
        //skip, berarti tidak punya anak
        System.out.println("Err : "+t.getMessage());
      }
      
      OBDal.getInstance().commitAndClose();
    }
    
    public void validateCarStatusList(String header_id, JSONObject hasilRetrieve)
            throws Exception, OBException {
        List<String> tempValidDocumentUpdateLine = new ArrayList<String>();
        JSONArray carList = (JSONArray) hasilRetrieve.get("data");
        // OBCriteria<TmcListChildAcc> tmcNotExsListChildAcc = null;
        for (int i = 0; i < carList.length(); i++) {
            String imei = carList.getJSONObject(i).get("imei").toString();
            String device_info = carList.getJSONObject(i).get("device_info").toString();
            // String gps_time = carList.getJSONObject(i).get("gps_time").toString();
            String sys_time = carList.getJSONObject(i).get("sys_time").toString();
            // String heart_time = carList.getJSONObject(i).get("heart_time").toString();
            String server_time = carList.getJSONObject(i).get("server_time").toString();
            // String lng = carList.getJSONObject(i).get("lng").toString();
            // String lat = carList.getJSONObject(i).get("lat").toString();
            // String course = carList.getJSONObject(i).get("course").toString();
            String speed = carList.getJSONObject(i).get("speed").toString();
            // String acc = carList.getJSONObject(i).get("acc").toString();
            // String acc_seconds = carList.getJSONObject(i).get("acc_seconds").toString();
            // String seconds = carList.getJSONObject(i).get("seconds").toString();
            
            OBCriteria<TmcCar> tmcCarCriteria = OBDal.getInstance().createCriteria(TmcCar.class);
            tmcCarCriteria.add(Restrictions.eq(TmcCar.PROPERTY_IMEI, imei));
            tmcCarCriteria.add(Restrictions.eq(TmcCar.PROPERTY_CREATEDBY, COOTRACK_USER));
            
            if (tmcCarCriteria.count() > 0) { // bila ada maka data tersebut sinkron, tinggal tentukan
                // update atau insert
                // perhitungan status disini
                String statusCategory = "";
                int hourInterval = new OpenApiUtils().getIntervalFromUnix(Long.parseLong(sys_time.trim()),
                        Long.parseLong(server_time.trim()), "hours");
                int minInterval = new OpenApiUtils().getIntervalFromUnix(Long.parseLong(sys_time.trim()),
                        Long.parseLong(server_time.trim()), "minutes");
                int dayInterval = new OpenApiUtils().getIntervalFromUnix(Long.parseLong(sys_time.trim()),
                        Long.parseLong(server_time.trim()), "days");
                
                int nearExpired = new OpenApiUtils().getIntervalFromUnix(Long.parseLong(server_time.trim()),
                        tmcCarCriteria.list().get(0).getOUTTime(), "days");
                
                statusCategory = getStatusCategory(device_info, dayInterval, hourInterval, speed,
                        nearExpired);
                
                // if (statusCategory != null ) {
                
                OBCriteria<TmcDocumentUpdateLine> tmcDocumentUpdateLine = OBDal.getInstance()
                        .createCriteria(TmcDocumentUpdateLine.class);
                // filter header nya [ok]
                TmcDocumentUpdate header = getHeaderInstance(header_id);
                tmcDocumentUpdateLine
                        .add(Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_TMCDOCUMENTUPDATE, header));
                tmcDocumentUpdateLine
                        .add(Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_CREATEDBY, COOTRACK_USER));
                tmcDocumentUpdateLine.add(
                        Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_TMCCAR, tmcCarCriteria.list().get(0)));
                
                if (tmcDocumentUpdateLine.list().isEmpty()) { // data belum ada
                    // ini insert
                    TmcDocumentUpdateLine newTmcDocumentUpdateLine = OBProvider.getInstance()
                            .get(TmcDocumentUpdateLine.class);
                    
                    newTmcDocumentUpdateLine.setActive(true);
                    newTmcDocumentUpdateLine.setTMCDocumentupdate(header);// set header nya
                    newTmcDocumentUpdateLine.setCustomerName(tmcCarCriteria.list().get(0).getBpartner());
                    newTmcDocumentUpdateLine.setTMCCar(tmcCarCriteria.list().get(0));
                    // temporary
                    // newTmcDocumentUpdateLine.setKeterangan("Near Exp - " + nearExpired);
                    
                    newTmcDocumentUpdateLine.setStatus(statusCategory);
                    
                    OBDal.getInstance().save(newTmcDocumentUpdateLine);
                    OBDal.getInstance().flush();
                    
                    tempValidDocumentUpdateLine.add(newTmcDocumentUpdateLine.getId()); // untuk data yg
                    // sinkron berdasar
                    // static 8 jam, dll
                    // dan ada
                    // end insert
                } else {
                    tmcDocumentUpdateLine.list().get(0)
                            .setCustomerName(tmcCarCriteria.list().get(0).getBpartner());
                    tmcDocumentUpdateLine.list().get(0).setTMCCar(tmcCarCriteria.list().get(0));
                    // temporary
                    // tmcDocumentUpdateLine.list().get(0).setKeterangan("Near Exp - " + nearExpired);
                    
                    tmcDocumentUpdateLine.list().get(0).setStatus(statusCategory);
                    
                    OBDal.getInstance().save(tmcDocumentUpdateLine.list().get(0));
                    OBDal.getInstance().flush();
                    
                    tempValidDocumentUpdateLine.add(tmcDocumentUpdateLine.list().get(0).getId()); // untuk
                    // data yg
                    // sinkron
                    // berdasar
                    // static 8
                    // jam, dll
                    // dan ada
                }
                // } //end record perlu di masukan berdasar status yg ditentukan
                
            }
        }
        
        TmcDocumentUpdate header = getHeaderInstance(header_id);
        //
        // adegan menghapus record yg tidak valid berdasarkan status yg ditentukan
        OBCriteria<TmcDocumentUpdateLine> delNotinCritTmcDocumentUpdateLine = OBDal.getInstance()
                .createCriteria(TmcDocumentUpdateLine.class);
        
        delNotinCritTmcDocumentUpdateLine
                .add(Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_TMCDOCUMENTUPDATE, header));
        delNotinCritTmcDocumentUpdateLine
                .add(Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_CREATEDBY, COOTRACK_USER));
        
        delNotinCritTmcDocumentUpdateLine
                .add(Restrictions.isNull(TmcDocumentUpdateLine.PROPERTY_STATUS));
        
        for (TmcDocumentUpdateLine removeTmcDocumentUpdateLine : delNotinCritTmcDocumentUpdateLine
                .list()) {
            OBDal.getInstance().remove(removeTmcDocumentUpdateLine);
            OBDal.getInstance().flush();
        }
        
        // adegan menghapus record yg ada table TMC_DocumentUpdateLine tapi tidak ada di TMC_Car
        OBCriteria<TmcDocumentUpdateLine> delTmcDocumentUpdateLine = OBDal.getInstance()
                .createCriteria(TmcDocumentUpdateLine.class);
        // filter headernya [ok]
        delTmcDocumentUpdateLine
                .add(Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_TMCDOCUMENTUPDATE, header));
        delTmcDocumentUpdateLine
                .add(Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_CREATEDBY, COOTRACK_USER));
        
        OBCriteria<TmcCar> seluruhMobilDiTmcCar = OBDal.getInstance().createCriteria(TmcCar.class);
        seluruhMobilDiTmcCar.add(Restrictions.eq(TmcCar.PROPERTY_CREATEDBY, COOTRACK_USER));
        
        delTmcDocumentUpdateLine.add(Restrictions
                .not(Restrictions.in(TmcDocumentUpdateLine.PROPERTY_TMCCAR, seluruhMobilDiTmcCar.list()))); //
        for (TmcDocumentUpdateLine removeTmcDocumentUpdateLine : delTmcDocumentUpdateLine.list()) {
            OBDal.getInstance().remove(removeTmcDocumentUpdateLine);
            OBDal.getInstance().flush();
        }
        
        OBDal.getInstance().commitAndClose();
    }
    
    private TmcDocumentUpdate getHeaderInstance(String header_id) {
        TmcDocumentUpdate header = OBDal.getInstance().get(TmcDocumentUpdate.class, header_id);
        return header;
    }
    
    private String getStatusCategory(String device_info, int dayInterval, int hourInterval,
            String speed, int nearExpired) {
        String hasil = "";
        
        // static 8 hours ++
        if ((device_info.equals("0")) && (hourInterval >= 8)
                && (dayInterval == 0) /* && (speed.equals("0")) */ ) {
            hasil = "Static 8 Hours";
        }
        // static 1 days ++
        else if ((device_info.equals("0")) && (dayInterval >= 1) /* && (speed.equals("0")) */ ) {
            hasil = "Static 1 Day";
        }
        // offline 1 ~ 59 days
        else if ((device_info.equals("3")) && (dayInterval < 60) /* && (speed.equals("0")) */ ) {
            hasil = "Offline 1 Days";
        }
        // offline 60 days ++
        else if ((device_info.equals("3")) && (dayInterval >= 60) /* && (speed.equals("0")) */ ) {
            hasil = "Expired Payment";
        }
        // Arrear Payment
        else if ((device_info.equals("2")) /* && (speed.equals("0")) */ ) {
            hasil = "Arrear Payment";
        } else {
            hasil = null;
        }
        
        /*
        * if ( (device_info.equals("0")) && (hourInterval >= 8) && (dayInterval == 0) /*&&
        * (speed.equals("0")) ) { hasil = "Static 8 Hours"; }
        */
        
        // Expired Payment
        // Arrear Payment
        return hasil;
    }
}
/*ArrayList<String> tempIdDataServer = new ArrayList<String>();
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
                    for (TmcDocumentUpdateLine tmcListChildCar : removeRecord.getTmcDocumentUpdateLineList()) {
                        OBDal.getInstance().remove(tmcListChildCar);
                        OBDal.getInstance().flush();
                    }
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
                for (TmcDocumentUpdateLine tmcListChildCar : removeLine.getTmcDocumentUpdateLineList()) {
                    OBDal.getInstance().remove(tmcListChildCar);
                    OBDal.getInstance().flush();
                }
                OBDal.getInstance().remove(removeLine);
                OBDal.getInstance().flush();
            }
            OBDal.getInstance().remove(removeRecord);
            OBDal.getInstance().flush();
        }
        
        OBDal.getInstance().commitAndClose();
        */