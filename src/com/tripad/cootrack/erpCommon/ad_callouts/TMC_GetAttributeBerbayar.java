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
// classes required to retrieve product category data from the
// database using the DAL
//import org.openbravo.dal.service.OBDal;
//import org.openbravo.model.common.plm.ProductCategory;

// the name of the class corresponds to the filename that holds it
// hence, modules/modules/org.openbravo.howtos/src/org/openbravo/howtos/ad_callouts/ProductConstructSearchKey.java.
// The class must extend SimpleCallout
public class TMC_GetAttributeBerbayar extends SimpleCallout {

  private static final long serialVersionUID = 1L;

  private static  Logger log = Logger.getLogger(TMC_GetAttributeBerbayar.class);

  @Override
  protected void execute(CalloutInfo info) throws ServletException {

 try{

  String strBusinessPartnerId = info.getStringParameter("inpcBpartnerId", null);
  String strCarId = info.getStringParameter("inptmcCarId", null);


DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
  // Deklasrasi atribut berbayar
Date today = Calendar.getInstance().getTime();

  String tglIsiPulsaReguler = "";
  BigDecimal nominalPengisianReguler = BigDecimal.ZERO;
  String tglIsiPulsaQuota = "";
  BigDecimal nominalPengisianQuota = BigDecimal.ZERO;
  String maintenanceDateTo = "";
  String maintenanceDateFrom = "";
  Long pengisianKe = new Long("0");

   String a = "";
   String b = "";
   String c = "";
   String d = "";

  BigDecimal e = BigDecimal.ZERO;
  BigDecimal f = BigDecimal.ZERO;
  Long g = new Long("0");


  StringBuilder whereClauses = new StringBuilder();
  whereClauses.append(" as f");
  whereClauses.append(" WHERE f.creationDate = (SELECT MAX(ff.creationDate) FROM Tmc_DocumentUpdateLine ff WHERE ff.customerName = '"+strBusinessPartnerId+"' AND ff.tMCCar = '"+strCarId+"' ) ");
  whereClauses.append(" ORDER BY customerName ASC");


  final OBQuery<TmcDocumentUpdateLine> obc = OBDal.getInstance().createQuery(TmcDocumentUpdateLine.class,whereClauses.toString());

  
  for (TmcDocumentUpdateLine documentLine : obc.list()){

     try{

     	  	    a = dateFormat.format(documentLine.getTGLIsiPulsaReg()).toString();
  	            b = dateFormat.format(documentLine.getTGLIsiPulsaQuota()).toString();
  	            c = dateFormat.format(documentLine.getMaintenanceDateTo()).toString();
  	            d = dateFormat.format(documentLine.getMaintenanceDateFrom()).toString();

  				e = documentLine.getNOMIsiPulsaReg();		
				f = documentLine.getNOMIsiPulsaQuota();
				g = documentLine.getPengisianke();


     }catch (Exception m) {
            log.error("Error processing request: " + m.getMessage(), m);
        }


  }

    try{


                		tglIsiPulsaReguler = a;
                	    tglIsiPulsaQuota = b;
                		maintenanceDateTo = c;
                		maintenanceDateFrom = d;         
                		nominalPengisianReguler = e;            
                		nominalPengisianQuota = f;
                		pengisianKe = g;


               
   }catch(Exception ex){
   	log.error("Error processing request: " + ex.getMessage(), ex);
   }

    info.addResult("inptglIsiPulsaReg", tglIsiPulsaReguler.toString());
    info.addResult("inpnomIsiPulsaReg", nominalPengisianReguler.toString());
    info.addResult("inptglIsiPulsaQuota", tglIsiPulsaQuota.toString());
    info.addResult("inpnomIsiPulsaQuota", nominalPengisianQuota.toString());
    info.addResult("inpmaintenancedateto", maintenanceDateTo.toString());
    info.addResult("inpmaintenancedatefrom", maintenanceDateFrom.toString());
    info.addResult("inppengisianke", pengisianKe);

}catch(Exception e){
   	log.error("Error processing request: " + e.getMessage(), e);
 }
  
  }

}
