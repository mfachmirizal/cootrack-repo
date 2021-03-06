/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.erpCommon.process;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openbravo.client.application.ApplicationConstants;
import org.openbravo.client.application.process.BaseProcessActionHandler;
import org.openbravo.dal.service.OBDal;

/**
 *
 * @author mfachmirizal
 */

import com.tripad.cootrack.data.TmcDocumentUpdateLine;

/**
 * @author iperdomo
 *
 */
public class PAUUpdateCustomerMaintenanceActivities extends BaseProcessActionHandler {

  private static Logger log = Logger.getLogger(PAUUpdateCustomerMaintenanceActivities.class);

  @Override
  protected JSONObject doExecute(Map<String, Object> parameters, String content) {
    JSONObject result = new JSONObject();
    JSONArray actions = new JSONArray();
    JSONObject msgInBPTab = new JSONObject();
    JSONObject msgInBPTabAction = new JSONObject();

    try {
      JSONObject request = new JSONObject(content);

      log.info(">> parameters: " + parameters);
      // log.info(">> content:" + content);

      log.info(">> request : " + request.toString());

      // _selection contains the rows that the user selected.
      /*
       * JSONArray selection = new JSONArray(
       * request.getString(ApplicationConstants.SELECTION_PROPERTY));
       */
      JSONArray selection = request.getJSONObject("_params")
          .getJSONObject("prm_pau_customer_maintenance")
          .getJSONArray(ApplicationConstants.SELECTION_PROPERTY);

      log.info(">> selected: " + selection);
      String id = null;

      for (int i = 0; i < selection.length(); i++) {
        log.info("LENGTH : " + selection.length());
        log.info(">> Nilai id : " + selection.getJSONObject(i).get("id").toString());
        log.info(">> Nilai p_gps_ditelpon : "
            + selection.getJSONObject(i).get("gPSDiTelepon").toString());
        log.info(">> Nilai gPSDiSMS : " + selection.getJSONObject(i).get("gPSDiSMS").toString());
        log.info(">> Nilai masaAktif : " + selection.getJSONObject(i).get("masaAktif").toString());
        log.info(">> Nilai Sisa Pulsa : " + selection.getJSONObject(i).get("sisaPulsa").toString());
        log.info(">> Nilai sisaQuota : " + selection.getJSONObject(i).get("sisaQuota").toString());
        log.info(">> Nilai analisaProblem : "
            + selection.getJSONObject(i).get("analisaProblem").toString());
        log.info(">> Nilai solvingBySystem : "
            + selection.getJSONObject(i).get("solvingBySystem").toString());
        log.info(">> Nilai result : " + selection.getJSONObject(i).get("result").toString());
        log.info(">> Nilai byPhone : " + selection.getJSONObject(i).get("byPhone").toString());
        log.info(">> Nilai bySms : " + selection.getJSONObject(i).get("bySMS").toString());
        log.info(">> Nilai maintenanceDateFrom : "
            + selection.getJSONObject(i).get("maintenanceDateFrom").toString());
        log.info(">> Nilai maintenanceDateTo : "
            + selection.getJSONObject(i).get("maintenanceDateTo").toString());
        log.info(">> Nilai jawabanCustomer : "
            + selection.getJSONObject(i).get("jawabanCustomer").toString());
        log.info(
            ">> Nilai keterangan : " + selection.getJSONObject(i).get("keterangan").toString());
        id = (String) selection.getJSONObject(i).get("id");

        TmcDocumentUpdateLine tmcDocumentUpdateLine = OBDal.getInstance()
            .get(TmcDocumentUpdateLine.class, id);

        if (!selection.getJSONObject(i).get("gPSDiTelepon").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setGPSDiTelepon(selection.getJSONObject(i).get("gPSDiTelepon").toString());
        } else {
          tmcDocumentUpdateLine.setGPSDiTelepon(null);
        }

        if (!selection.getJSONObject(i).get("gPSDiSMS").toString().equals("null")) {
          tmcDocumentUpdateLine.setGPSDiSMS(selection.getJSONObject(i).get("gPSDiSMS").toString());
        } else {
          tmcDocumentUpdateLine.setGPSDiSMS(null);
        }

        if (!selection.getJSONObject(i).get("masaAktif").toString().equals("null")) {
          tmcDocumentUpdateLine.setMasaAktif(new SimpleDateFormat("yyyy-MM-dd")
              .parse(selection.getJSONObject(i).get("masaAktif").toString())); // 2016-01-07
        } else {
          tmcDocumentUpdateLine.setMasaAktif(null);
        }

        if (!selection.getJSONObject(i).get("sisaPulsa").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setSisaPulsa(new BigDecimal(selection.getJSONObject(i).get("sisaPulsa").toString()));
        } else {
          tmcDocumentUpdateLine.setSisaPulsa(null);
        }

        if (!selection.getJSONObject(i).get("sisaQuota").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setSisaQuota(new Long(selection.getJSONObject(i).get("sisaQuota").toString()));
        } else {
          tmcDocumentUpdateLine.setSisaQuota(null);
        }

        if (!selection.getJSONObject(i).get("analisaProblem").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setAnalisaProblem(selection.getJSONObject(i).get("analisaProblem").toString());
        } else {
          tmcDocumentUpdateLine.setAnalisaProblem(null);
        }

        if (!selection.getJSONObject(i).get("solvingBySystem").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setSolvingBySystem(selection.getJSONObject(i).get("solvingBySystem").toString());
        } else {
          tmcDocumentUpdateLine.setSolvingBySystem(null);
        }

        if (!selection.getJSONObject(i).get("result").toString().equals("null")) {
          tmcDocumentUpdateLine.setResult(selection.getJSONObject(i).get("result").toString());
        } else {
          tmcDocumentUpdateLine.setResult(null);
        }

        if (!selection.getJSONObject(i).get("byPhone").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setByPhone(selection.getJSONObject(i).get("byPhone").equals("Y") ? true : false);
        } else {
          tmcDocumentUpdateLine.setByPhone(null);
        }

        if (!selection.getJSONObject(i).get("bySMS").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setBySMS(selection.getJSONObject(i).get("bySMS").equals("Y") ? true : false);
        } else {
          tmcDocumentUpdateLine.setBySMS(null);
        }

        if (!selection.getJSONObject(i).get("maintenanceDateFrom").toString().equals("null")) {
          tmcDocumentUpdateLine.setMaintenanceDateFrom(new SimpleDateFormat("yyyy-MM-dd")
              .parse(selection.getJSONObject(i).get("maintenanceDateFrom").toString()));
        } else {
          tmcDocumentUpdateLine.setMaintenanceDateFrom(null);
        }

        if (!selection.getJSONObject(i).get("maintenanceDateTo").toString().equals("null")) {
          tmcDocumentUpdateLine.setMaintenanceDateTo(new SimpleDateFormat("yyyy-MM-dd")
              .parse(selection.getJSONObject(i).get("maintenanceDateTo").toString()));
        } else {
          tmcDocumentUpdateLine.setMaintenanceDateTo(null);
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime df = formatter
            .parseDateTime(selection.getJSONObject(i).get("maintenanceDateFrom").toString());
        DateTime dt = formatter
            .parseDateTime(selection.getJSONObject(i).get("maintenanceDateTo").toString());
        int selisihPengisian = (Months.monthsBetween(df, dt).getMonths()) + 1;
        // System.out.println("Selisih : " + selisihPengisian);
        // pengisian ke
        Long pengisianke = Long.parseLong(selection.getJSONObject(i).get("pengisianke").toString());
        if (!selection.getJSONObject(i).get("pengisianke").toString().equals("null")) {
          tmcDocumentUpdateLine.setPengisianke(pengisianke);
        } else {
          tmcDocumentUpdateLine.setPengisianke(new Long("0"));
          pengisianke = new Long("0");
        }

        if (selisihPengisian - pengisianke < 0) {
          // throw new Exception(
          // "Selisih Bulan Maintenance Date dikurangi pengisian, menghasilkan nilai minus !");
          result.put("retryExecution", true);

          JSONObject msg = new JSONObject();
          msg.put("severity", "error");
          msg.put("text",
              "Selisih Bulan Maintenance Date dikurangi pengisian menghasilkan nilai minus !");
          result.put("message", msg);
          OBDal.getInstance().rollbackAndClose();
          return result;
        }

        if (!selection.getJSONObject(i).get("jawabanCustomer").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setJawabanCustomer(selection.getJSONObject(i).get("jawabanCustomer").toString());
        } else {
          tmcDocumentUpdateLine.setJawabanCustomer(null);
        }

        if (!selection.getJSONObject(i).get("keterangan").toString().equals("null")) {
          tmcDocumentUpdateLine
              .setKeterangan(selection.getJSONObject(i).get("keterangan").toString());
        } else {
          tmcDocumentUpdateLine.setKeterangan(null);
        }

        // baru 18-maret-2016, penambahan 4 field baru

        if (!selection.getJSONObject(i).get("tGLIsiPulsaReg").toString().equals("null")) {
          tmcDocumentUpdateLine.setTGLIsiPulsaReg(new SimpleDateFormat("yyyy-MM-dd")
              .parse(selection.getJSONObject(i).get("tGLIsiPulsaReg").toString()));
        } else {
          tmcDocumentUpdateLine.setTGLIsiPulsaReg(null);
        }

        if (!selection.getJSONObject(i).get("nOMIsiPulsaReg").toString().equals("null")) {
          tmcDocumentUpdateLine.setNOMIsiPulsaReg(
              new BigDecimal(selection.getJSONObject(i).get("nOMIsiPulsaReg").toString()));
        } else {
          tmcDocumentUpdateLine.setNOMIsiPulsaReg(BigDecimal.ONE);
        }

        if (!selection.getJSONObject(i).get("tGLIsiPulsaQuota").toString().equals("null")) {
          tmcDocumentUpdateLine.setTGLIsiPulsaQuota(new SimpleDateFormat("yyyy-MM-dd")
              .parse(selection.getJSONObject(i).get("tGLIsiPulsaQuota").toString()));
        } else {
          tmcDocumentUpdateLine.setTGLIsiPulsaQuota(null);
        }

        if (!selection.getJSONObject(i).get("nOMIsiPulsaQuota").toString().equals("null")) {
          tmcDocumentUpdateLine.setNOMIsiPulsaQuota(
              new BigDecimal(selection.getJSONObject(i).get("nOMIsiPulsaQuota").toString()));
        } else {
          tmcDocumentUpdateLine.setNOMIsiPulsaQuota(BigDecimal.ONE);
        }

        tmcDocumentUpdateLine.setProcess(true);

        OBDal.getInstance().save(tmcDocumentUpdateLine);
        OBDal.getInstance().flush();
      }

      OBDal.getInstance().commitAndClose();

      msgInBPTab.put("msgType", "success");
      msgInBPTab.put("msgTitle", "Process Update");
      msgInBPTab.put("msgText", "Record Berhasil di Update");

      msgInBPTabAction.put("showMsgInView", msgInBPTab);

      actions.put(msgInBPTabAction);

      // request.put("MESSAGE", "Sukses !");
      result.put("responseActions", actions);

      return result;

    } catch (JSONException e) {
      OBDal.getInstance().rollbackAndClose();
      return new JSONObject();
    } catch (Exception t) {
      OBDal.getInstance().rollbackAndClose();
      log.error("Error processing request: " + t.getMessage(), t);
      return errorMessage(result, t.getMessage());
    }
  }

  private JSONObject errorMessage(JSONObject result, String msgParam) {
    try {
      JSONArray actions = new JSONArray();

      String pesan = "Error : " + msgParam;

      JSONObject msg = new JSONObject();
      msg.put("msgType", "error");
      // XXX: these two messages should be translatable, like OBEXAPP_MinGtMax above
      msg.put("msgTitle", "Error");
      msg.put("msgText", pesan);

      JSONObject msgTotalAction = new JSONObject();
      msgTotalAction.put("showMsgInProcessView", msg);

      actions.put(msgTotalAction);

      result.put("responseActions", actions);
    } catch (JSONException e) {
      return new JSONObject();
    }
    return result;
  }
}
