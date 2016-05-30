// the package name corresponds to the module's manual code folder
// created above
package com.tripad.cootrack.erpCommon.ad_callouts;

import javax.servlet.ServletException;
import com.tripad.cootrack.data.TmcDocumentUpdateLine;
import org.openbravo.utils.FormatUtilities;
import org.openbravo.erpCommon.ad_callouts.SimpleCallout;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import java.math.*;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.dal.service.OBQuery;

import java.math.BigDecimal;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;
// classes required to retrieve product category data from the
// database using the DAL
//import org.openbravo.dal.service.OBDal;
//import org.openbravo.model.common.plm.ProductCategory;

// the name of the class corresponds to the filename that holds it
// hence, modules/modules/org.openbravo.howtos/src/org/openbravo/howtos/ad_callouts/ProductConstructSearchKey.java.
// The class must extend SimpleCallout
public class TMC_CalculateCreditLimit extends SimpleCallout {

  private static final long serialVersionUID = 1L;

  private static  Logger log = Logger.getLogger(TMC_CalculateCreditLimit.class);

  @Override
  protected void execute(CalloutInfo info) throws ServletException {

 try{

  String strBusinessPartnerId = info.getStringParameter("inpcBpartnerId", null);
  String strCarId = info.getStringParameter("inptmcCarId", null);
  String a = info.getStringParameter("inpmaintenancedatefrom", null);
  String b = info.getStringParameter("inpmaintenancedateto", null);
  BigDecimal c = info.getBigDecimalParameter("inpbudget");


  BigDecimal nominalPengisianReguler = BigDecimal.ZERO;
  BigDecimal nominalPengisianQuota = BigDecimal.ZERO;


  BigDecimal nominalBudget = BigDecimal.ZERO;
  BigDecimal totalOrder = BigDecimal.ZERO;

  BusinessPartner parentBusinessPartner = null;

DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
  // Deklasrasi atribut berbayar
Date today = Calendar.getInstance().getTime();

int diffYear = 0;
int diffMonth = 0;


                    
                 Date dateFrom = dateFormat.parse(a);
                 Date dateEnd = dateFormat.parse(b);

                  Calendar startCalendar = Calendar.getInstance();
                  startCalendar.setTime(dateFrom);
                  Calendar endCalendar = Calendar.getInstance();
                  endCalendar.setTime(dateEnd);

                   diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                   diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

             nominalBudget = c;
      

        totalOrder = (nominalBudget);



        parentBusinessPartner = OBDal.getInstance().get(BusinessPartner.class, strBusinessPartnerId);
        BigDecimal creditUsed = parentBusinessPartner.getCreditUsed();
        BigDecimal totalSumOrder = totalOrder.subtract(new BigDecimal(diffMonth));
       
      if (totalSumOrder.intValue()  < creditUsed.abs().intValue()) {
               
       JSONArray actions = new JSONArray();

      String pesan ="Credit tidak cukup";

      JSONObject msg = new JSONObject();
      msg.put("msgType", "error");
      // XXX: these two messages should be translatable, like OBEXAPP_MinGtMax above
      msg.put("msgTitle", "Error");
      msg.put("msgText", pesan);

     }

               
   }catch(Exception ex){
   	log.error("Error processing request: " + ex.getMessage(), ex);
   }

     }

}