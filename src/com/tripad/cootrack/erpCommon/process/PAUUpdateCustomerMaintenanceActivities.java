/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.erpCommon.process;

/**
 *
 * @author mfachmirizal
 */

import com.tripad.cootrack.data.TmcDocumentUpdateLine;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.client.application.ApplicationConstants;
import org.openbravo.client.application.process.BaseProcessActionHandler;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.service.db.CallStoredProcedure;

/**
 * @author iperdomo
 *
 */
public class PAUUpdateCustomerMaintenanceActivities extends BaseProcessActionHandler {
    
    private static  Logger log = Logger.getLogger(PAUUpdateCustomerMaintenanceActivities.class);
    
    @Override
    protected JSONObject doExecute(Map<String, Object> parameters, String content) {
        try {
            JSONObject request = new JSONObject(content);
            
            log.info(">> parameters: " + parameters);
            // log.info(">> content:" + content);
            
            log.info(">> request : "+request.toString());
            
            // _selection contains the rows that the user selected.
            /*JSONArray selection = new JSONArray(
            request.getString(ApplicationConstants.SELECTION_PROPERTY));*/
            JSONArray selection = request.getJSONObject("_params").getJSONObject("prm_pau_customer_maintenance").getJSONArray(ApplicationConstants.SELECTION_PROPERTY) ;
            
            log.info(">> selected: " + selection);
            String id= null;
            String p_gps_ditelpon= null;
            String p_gps_disms= null;
            Date p_masa_aktif= null;
            BigDecimal p_sisa_pulsa= null;
            String p_sisa_quota= null;
            String p_analisa_problem= null;
            String p_solving_bysystem= null;
            String p_result = null;
            String p_by_phone = null;
            String p_by_sms = null;
            Date p_maintenancedate_from = null;
            Date p_maintenancedate_to= null;
            String p_jawaban_customer= null;
            String p_keterangan = null;
            
            for (int i = 0; i < selection.length(); i++) {
                log.info("LENGTH : "+selection.length());
                log.info(">> Nilai id : "+selection.getJSONObject(i).get("id").toString());
                log.info(">> Nilai p_gps_ditelpon : "+selection.getJSONObject(i).get("gPSDiTelepon").toString());
                log.info(">> Nilai gPSDiSMS : "+selection.getJSONObject(i).get("gPSDiSMS").toString());
                log.info(">> Nilai masaAktif : "+selection.getJSONObject(i).get("masaAktif").toString());
                log.info(">> Nilai Sisa Pulsa : "+selection.getJSONObject(i).get("sisaPulsa").toString());
                log.info(">> Nilai sisaQuota : "+selection.getJSONObject(i).get("sisaQuota").toString());
                log.info(">> Nilai analisaProblem : "+selection.getJSONObject(i).get("analisaProblem").toString());
                log.info(">> Nilai solvingBySystem : "+selection.getJSONObject(i).get("solvingBySystem").toString());
                log.info(">> Nilai result : "+selection.getJSONObject(i).get("result").toString());
                log.info(">> Nilai byPhone : "+selection.getJSONObject(i).get("byPhone").toString());
                log.info(">> Nilai bySms : "+selection.getJSONObject(i).get("bySMS").toString());
                log.info(">> Nilai maintenanceDateFrom : "+selection.getJSONObject(i).get("maintenanceDateFrom").toString());
                log.info(">> Nilai maintenanceDateTo : "+selection.getJSONObject(i).get("maintenanceDateTo").toString());
                log.info(">> Nilai jawabanCustomer : "+selection.getJSONObject(i).get("jawabanCustomer").toString());
                log.info(">> Nilai keterangan : "+selection.getJSONObject(i).get("keterangan").toString());
                id = (String) selection.getJSONObject(i).get("id");
                
                TmcDocumentUpdateLine tmcDocumentUpdateLine = OBDal.getInstance().get(TmcDocumentUpdateLine.class,id);
                
                
                if (!selection.getJSONObject(i).get("gPSDiTelepon").toString().equals("null")) {
                    tmcDocumentUpdateLine.setGPSDiTelepon(selection.getJSONObject(i).get("gPSDiTelepon").toString());
                } else {
                    tmcDocumentUpdateLine.setGPSDiTelepon(null);
                }
                
                if (!selection.getJSONObject(i).get("gPSDiSMS").toString().equals("null")) {
                    tmcDocumentUpdateLine.setGPSDiSMS(selection.getJSONObject(i).get("gPSDiSMS").toString());
                } else {
                    tmcDocumentUpdateLine.setGPSDiSMS(null);
                }
                
                
                if (!selection.getJSONObject(i).get("masaAktif").toString().equals("null")) {
                    tmcDocumentUpdateLine.setMasaAktif(new SimpleDateFormat("yyyy-MM-dd").parse(selection.getJSONObject(i).get("masaAktif").toString())); //2016-01-07
                } else {
                    tmcDocumentUpdateLine.setMasaAktif(null);
                }
                
                
                if (!selection.getJSONObject(i).get("sisaPulsa").toString().equals("null")) {
                    tmcDocumentUpdateLine.setSisaPulsa(new BigDecimal(selection.getJSONObject(i).get("sisaPulsa").toString()));
                } else {
                    tmcDocumentUpdateLine.setSisaPulsa(null);
                }
                
                
                if (!selection.getJSONObject(i).get("sisaQuota").toString().equals("null")) {
                    tmcDocumentUpdateLine.setSisaQuota(new Long(selection.getJSONObject(i).get("sisaQuota").toString()));
                } else {
                    tmcDocumentUpdateLine.setSisaQuota(null);
                }
                
                
                if (!selection.getJSONObject(i).get("analisaProblem").toString().equals("null")) {
                    tmcDocumentUpdateLine.setAnalisaProblem(selection.getJSONObject(i).get("analisaProblem").toString());
                } else {
                    tmcDocumentUpdateLine.setAnalisaProblem(null);
                }
                
                
                if (!selection.getJSONObject(i).get("solvingBySystem").toString().equals("null")) {
                    tmcDocumentUpdateLine.setSolvingBySystem(selection.getJSONObject(i).get("solvingBySystem").toString());
                } else {
                    tmcDocumentUpdateLine.setSolvingBySystem(null);
                }
                
                
                if (!selection.getJSONObject(i).get("result").toString().equals("null")) {
                    tmcDocumentUpdateLine.setResult(selection.getJSONObject(i).get("result").toString());
                } else {
                    tmcDocumentUpdateLine.setResult(null);
                }
                
                
                
                if (!selection.getJSONObject(i).get("byPhone").toString().equals("null")) {
                    tmcDocumentUpdateLine.setByPhone(selection.getJSONObject(i).get("byPhone").equals("Y") ? true : false);
                } else {
                    tmcDocumentUpdateLine.setByPhone(null);
                }
                
                
                
                if (!selection.getJSONObject(i).get("bySMS").toString().equals("null")) {
                    tmcDocumentUpdateLine.setBySMS(selection.getJSONObject(i).get("bySMS").equals("Y") ? true : false);
                } else {
                    tmcDocumentUpdateLine.setBySMS(null);
                }
                
                
                if (!selection.getJSONObject(i).get("maintenanceDateFrom").toString().equals("null")) {
                    tmcDocumentUpdateLine.setMaintenanceDateFrom( new SimpleDateFormat("yyyy-MM-dd").parse(selection.getJSONObject(i).get("maintenanceDateFrom").toString()));
                } else {
                    tmcDocumentUpdateLine.setMaintenanceDateFrom(null);
                }
                
                
                
                if (!selection.getJSONObject(i).get("maintenanceDateTo").toString().equals("null")) {
                    tmcDocumentUpdateLine.setMaintenanceDateTo( new SimpleDateFormat("yyyy-MM-dd").parse(selection.getJSONObject(i).get("maintenanceDateTo").toString()));
                } else {
                    tmcDocumentUpdateLine.setMaintenanceDateTo( null );
                }
                
                
                
                if (!selection.getJSONObject(i).get("jawabanCustomer").toString().equals("null")) {
                    tmcDocumentUpdateLine.setJawabanCustomer(selection.getJSONObject(i).get("jawabanCustomer").toString());
                } else {
                    tmcDocumentUpdateLine.setJawabanCustomer(null);
                }
                
                
                if (!selection.getJSONObject(i).get("keterangan").toString().equals("null")) {
                    tmcDocumentUpdateLine.setKeterangan(selection.getJSONObject(i).get("keterangan").toString());
                } else {
                    tmcDocumentUpdateLine.setKeterangan(null);
                }
                
				tmcDocumentUpdateLine.setProcess(true);
                
                OBDal.getInstance().save(tmcDocumentUpdateLine);
                OBDal.getInstance().flush();
            }
            
            
            /*// _allRows contains all the rows available in the grid
            JSONArray allRows = new JSONArray(request.getString(ApplicationConstants.ALL_ROWS_PARAM));
            log.info(">> allRows: " + allRows);
            */
////
////            List<Object> params = new ArrayList<Object>();
////            params.add(id);
////            params.add(p_gps_ditelpon);
////            params.add(p_gps_disms);
////            params.add(p_masa_aktif);
////            params.add(p_sisa_pulsa);
////            log.info("id dan pulksa : "+id+" "+p_sisa_pulsa);
////            params.add(p_sisa_quota);
////            params.add(p_analisa_problem);
////            params.add(p_solving_bysystem);
////            params.add(p_result);
////            params.add(p_by_phone);
////            params.add(p_by_sms);
////            params.add(p_maintenancedate_from);
////            params.add(p_maintenancedate_to);
////            params.add(p_jawaban_customer);
////            params.add(p_keterangan);
////
////
////            CallStoredProcedure.getInstance().call("tmc_maintenanceprocess", params, null, true, false);
//
//            OBDal.getInstance().flush();
            OBDal.getInstance().commitAndClose();
            
            request.put("MESSAGE", "Sukses !");
            return request;
        } catch (Exception e) {
            log.error("Error processing request: " + e.getMessage(), e);
        }
        return new JSONObject();
    }
    
}

