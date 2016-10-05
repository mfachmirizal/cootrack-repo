
package com.tripad.cootrack.erpCommon.ad_callouts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.ad_callouts.SimpleCallout;
import org.openbravo.model.common.businesspartner.BusinessPartner;

import com.tripad.cootrack.data.TmcCar;
import com.tripad.cootrack.data.TmcDocumentUpdateLine;

// the name of the class corresponds to the filename that holds it
// hence, modules/modules/org.openbravo.howtos/src/org/openbravo/howtos/ad_callouts/ProductConstructSearchKey.java.
// The class must extend SimpleCallout
public class TMC_GetAttributeBerbayar extends SimpleCallout {

  private static final long serialVersionUID = 1L;

  private static Logger log = Logger.getLogger(TMC_GetAttributeBerbayar.class);

  @Override
  protected void execute(CalloutInfo info) throws ServletException {
    try {
      String strBusinessPartnerId = info.getStringParameter("inpcBpartnerId", null);
      String strCarId = info.getStringParameter("inptmcCarId", null);

      DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
      TmcCar car = OBDal.getInstance().get(TmcCar.class, strCarId);
      BusinessPartner bp = OBDal.getInstance().get(BusinessPartner.class, strBusinessPartnerId);

      OBCriteria<TmcDocumentUpdateLine> tmcDocumentUpdateLineLatest = OBDal.getInstance()
          .createCriteria(TmcDocumentUpdateLine.class);
      tmcDocumentUpdateLineLatest.add(Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_TMCCAR, car));
      tmcDocumentUpdateLineLatest
          .add(Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_CUSTOMERNAME, bp));

      // berdasar status : Maintenance Pulsa dan Quota
       tmcDocumentUpdateLineLatest.add(
       Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_STATUS, "Maintenance Pulsa atau Quota"));
       
      // order dari updated date yg paling besar
      tmcDocumentUpdateLineLatest.addOrderBy(TmcDocumentUpdateLine.PROPERTY_UPDATED, false);
      
      // Berdasarkan data yang sudah dibuat sales ordernya
             tmcDocumentUpdateLineLatest.add(
       Restrictions.eq(TmcDocumentUpdateLine.PROPERTY_ISSALESORDER,true));
      // set maksimum data yg keluar
      tmcDocumentUpdateLineLatest.setMaxResults(1);

      for (TmcDocumentUpdateLine prevDocument : tmcDocumentUpdateLineLatest.list()) {
        info.addResult("inptglIsiPulsaRegSebelumnya",
            dateFormat.format(prevDocument.getTGLIsiPulsaReg()).toString());
        info.addResult("inpnomIsiPulsaReg", prevDocument.getNOMIsiPulsaReg().toString());
        info.addResult("inptglIsiPulsaQuotaSebelumnya",
            dateFormat.format(prevDocument.getTGLIsiPulsaQuota()).toString());
        info.addResult("inpnomIsiPulsaQuota", prevDocument.getNOMIsiPulsaQuota().toString());
        info.addResult("inpmaintenancedateto",
            dateFormat.format(prevDocument.getMaintenanceDateTo()).toString());
        info.addResult("inpmaintenancedatefrom",
            dateFormat.format(prevDocument.getMaintenanceDateFrom()).toString());
        Long pengisianKe = prevDocument.getPengisianke() + new Long("1");
        info.addResult("inppengisianke", pengisianKe.toString());
        info.addResult("inpbudget", prevDocument.getBudget().toString());
        info.addResult("inpprofit", prevDocument.getProfit().toString());
        //tambahan callout field saldo awal (credit awal)
        info.addResult("inpcreditAwal", prevDocument.getCreditAwal().toString());
      }

    } catch (Exception e) {
      System.out.println("Error processing request: " + e.getMessage());
      log.error("Error processing request: " + e.getMessage(), e);
    }

  }

}
