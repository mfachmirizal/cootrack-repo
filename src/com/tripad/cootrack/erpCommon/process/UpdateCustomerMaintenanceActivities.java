package com.tripad.cootrack.erpCommon.process;

import com.tripad.cootrack.data.TmcDocumentUpdate;
import com.tripad.cootrack.data.TmcDocumentUpdateLine;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.openbravo.dal.core.DalUtil;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.CallStoredProcedure;
import org.openbravo.service.db.DalBaseProcess;

public class UpdateCustomerMaintenanceActivities extends DalBaseProcess {
    
    public void doExecute(ProcessBundle bundle) throws Exception {
        try {
            
            // retrieve the parameters from the bundle
            final String strRecordId = (String) bundle.getParams().get("TMC_Documentupdateline_ID");
            final String organizationId = (String) bundle.getParams().get("adOrgId");
            final String tabId = (String) bundle.getParams().get("tabId");
            
            final String p_gps_ditelpon = (String) bundle.getParams().get("p_gps_ditelpon");
            final String p_gps_disms = (String) bundle.getParams().get("p_gps_disms");
            final Date p_masa_aktif = (Date) bundle.getParams().get("p_masa_aktif");
            final BigDecimal p_sisa_pulsa = (BigDecimal) bundle.getParams().get("p_sisa_pulsa");
            final String p_sisa_quota = (String) bundle.getParams().get("p_sisa_quota");
            final String p_analisa_problem = (String) bundle.getParams().get("p_analisa_problem");
            final String p_solving_bysystem = (String) bundle.getParams().get("p_solving_bysystem");
            final String p_result = (String) bundle.getParams().get("p_result");
            final String p_by_phone = (String) bundle.getParams().get("p_by_phone");
            final String p_by_sms = (String) bundle.getParams().get("p_by_sms");
            final Date p_maintenancedate_from = (Date) bundle.getParams().get("p_maintenancedate_from");
            final Date p_maintenancedate_to = (Date) bundle.getParams().get("p_maintenancedate_to");
            final String p_jawaban_customer = (String) bundle.getParams().get("p_jawaban_customer");
            final String p_keterangan = (String) bundle.getParams().get("p_keterangan");
/*            
(p_id character varying, 
p_gps_ditelpon character varying, 
p_gps_disms character varying, 
p_masa_aktif timestamp without time zone, 
p_sisa_pulsa numeric, 
p_sisa_quota character varying, 
p_analisa_problem character varying, 
p_solving_bysystem character varying, 
p_result character varying, 
p_by_phone character varying, 
p_by_sms character varying, 
p_maintenancedate_from timestamp without time zone, 
p_maintenancedate_to timestamp without time zone, 
p_jawaban_customer character varying, p_keterangan character varying)
*/            
            // implement your process here
            List<Object> params = new ArrayList<Object>();
            params.add(strRecordId);
            params.add(p_gps_ditelpon);
            params.add(p_gps_disms);
            params.add(p_masa_aktif);
            params.add(p_sisa_pulsa);
            params.add(p_sisa_quota);
            params.add(p_analisa_problem);
            params.add(p_solving_bysystem);
            params.add(p_result);
            params.add(p_by_phone);
            params.add(p_by_sms);           
            params.add(p_maintenancedate_from);
            params.add(p_maintenancedate_to);
            params.add(p_jawaban_customer);
            params.add(p_keterangan);

            
            CallStoredProcedure.getInstance().call("tmc_maintenanceprocess", params, null, true, false);
            // Show a result
            final StringBuilder sb = new StringBuilder();
            sb.append("Read information:<br/>");
            sb.append(bundle.getParamsDeflated());
            
            
            // OBError is also used for successful results
            final OBError msg = new OBError();
            msg.setType("Success");
            msg.setTitle("Read parameters!");
            msg.setMessage(sb.toString());
            
            bundle.setResult(msg);
            
        } catch (final Exception e) {
            e.printStackTrace(System.err);
            final OBError msg = new OBError();
            msg.setType("Error");
            msg.setMessage(e.getMessage());
            msg.setTitle("Error occurred");
            bundle.setResult(msg);
        }
    }
}