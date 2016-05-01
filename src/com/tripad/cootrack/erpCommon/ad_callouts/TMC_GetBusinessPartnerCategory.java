// the package name corresponds to the module's manual code folder
// created above
package com.tripad.cootrack.erpCommon.ad_callouts;

import javax.servlet.ServletException;

import org.openbravo.utils.FormatUtilities;
import org.openbravo.erpCommon.ad_callouts.SimpleCallout;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import java.math.*;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
// classes required to retrieve product category data from the
// database using the DAL
//import org.openbravo.dal.service.OBDal;
//import org.openbravo.model.common.plm.ProductCategory;

// the name of the class corresponds to the filename that holds it
// hence, modules/modules/org.openbravo.howtos/src/org/openbravo/howtos/ad_callouts/ProductConstructSearchKey.java.
// The class must extend SimpleCallout
public class TMC_GetBusinessPartnerCategory extends SimpleCallout {

  private static final long serialVersionUID = 1L;

  @Override
  protected void execute(CalloutInfo info) throws ServletException {

 

  String strBusinessPartnerId = info.getStringParameter("inpcBpartnerId", null);

  BusinessPartner bpartner = OBDal.getInstance().get(BusinessPartner.class, strBusinessPartnerId);


    info.addResult("inpcBpGroupId", bpartner.getBusinessPartnerCategory().getId());

  }
}
