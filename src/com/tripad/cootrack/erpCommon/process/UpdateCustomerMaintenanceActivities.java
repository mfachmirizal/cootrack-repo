package com.tripad.cootrack.erpCommon.process;

import com.tripad.cootrack.data.TmcDocumentUpdate;
import com.tripad.cootrack.data.TmcDocumentUpdateLine;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.service.db.DalBaseProcess;

public class UpdateCustomerMaintenanceActivities extends DalBaseProcess {
 
  public void doExecute(ProcessBundle bundle) throws Exception {
    try {
 
      // retrieve the parameters from the bundle
      /*final String strTmcDocumentUpdateLineId = (String) bundle.getParams().get("cBpartnerId");
      final String organizationId = (String) bundle.getParams().get("adOrgId");
      final String tabId = (String) bundle.getParams().get("tabId"); 
 
      final String myString = (String) bundle.getParams().get("mystring");
 */
      // implement your process here
 
      // Show a result
      final StringBuilder sb = new StringBuilder();
      sb.append("Read information:<br/>");
      sb.append(bundle.getParamsDeflated());
   /*   if (bPartnerId != null) {
        final TmcDocumentUpdateLine tmcDocumentUpdateLine = OBDal.getInstance().get(TmcDocumentUpdateLine.class, bPartnerId);
        sb.append("tmcDocumentUpdateLine : " + tmcDocumentUpdateLine.getIdentifier() + "<br/>");
      }
      if (organizationId != null) {
        final Organization organization = OBDal.getInstance().get(Organization.class,
            organizationId);
        sb.append("Organization: " + organization.getIdentifier() + "<br/>");
      }
      sb.append("MyString: " + myString + "<br/>");
 */
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