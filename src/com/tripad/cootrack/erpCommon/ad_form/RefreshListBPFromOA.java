/*
*************************************************************************
* The contents of this file are subject to the Openbravo  Public  License
* Version  1.0  (the  "License"),  being   the  Mozilla   Public  License
* Version 1.1  with a permitted attribution clause; you may not  use this
* file except in compliance with the License. You  may  obtain  a copy of
* the License at http://www.openbravo.com/legal/license.html
* Software distributed under the License  is  distributed  on  an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific  language  governing  rights  and  limitations
* under the License.
* The Original Code is Openbravo ERP.
* The Initial Developer of the Original Code is Openbravo SLU
* All portions are Copyright (C) 2010-2014 Openbravo SLU
* All Rights Reserved.
* Contributor(s):  ______________________________________.
*************************************************************************
*/

package com.tripad.cootrack.erpCommon.ad_form;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.base.exception.OBException;
import org.openbravo.base.secureApp.HttpSecureAppServlet;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.base.session.OBPropertiesProvider;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.businessUtility.WindowTabs;
import org.openbravo.erpCommon.utility.DateTimeData;
import org.openbravo.erpCommon.utility.LeftTabsBar;
import org.openbravo.erpCommon.utility.NavigationBar;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.erpCommon.utility.ToolBar;
import org.openbravo.erpCommon.utility.Utility;
import org.openbravo.model.ad.access.User;
import org.openbravo.xmlEngine.XmlDocument;

import com.tripad.cootrack.utility.CootrackHttpClient;
import com.tripad.cootrack.utility.OpenApiUtils;
import com.tripad.cootrack.utility.ResponseResultToDB;
import com.tripad.cootrack.utility.exception.CustomJsonErrorResponseException;

public class RefreshListBPFromOA extends HttpSecureAppServlet {
  private static final long serialVersionUID = 1L;
  private final String THIS_CLASS = "RefreshListBPFromOA";
  private final String THIS_TEMPLATE = "com/tripad/cootrack/erpCommon/ad_form/" + THIS_CLASS;
  private final String THIS_PACKAGE_CLASS = "com.tripad.cootrack.erpCommon.ad_form." + THIS_CLASS;

  //User COOTRACK_USER = OBContext.getOBContext().getUser();
  //
  //    CootrackHttpClient con = new CootrackHttpClient();
  //    OpenApiUtils utils = new OpenApiUtils(con);

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    VariablesSecureApp vars = new VariablesSecureApp(request);

    if (vars.commandIn("DEFAULT")) {
      String strOrgId = vars.getRequestGlobalVariable("inpadOrgId", "RefreshListBPFromOA|Org");
      String strWindowId = vars.getRequestGlobalVariable("inpwindowId",
          "RefreshListBPFromOA|windowId");
      String strTabId = vars.getRequestGlobalVariable("inpTabId", "RefreshListBPFromOA|tabId");
      String strId = "";//vars.getStringParameter("inpfinFinancialAccountId");
      /*      final int accesslevel = 3;

      if ((org.openbravo.erpCommon.utility.WindowAccessData.hasReadOnlyAccess(this, vars.getRole(),
      strTabId))
      || !(Utility.isElementInList(
      Utility.getContext(this, vars, "#User_Client", strWindowId, accesslevel),
      vars.getClient())
      && Utility.isElementInList(
      Utility.getContext(this, vars, "#User_Org", strWindowId, accesslevel),
      strOrgId))) {
      OBError myError = Utility.translateError(this, vars, vars.getLanguage(),
      Utility.messageBD(this, "NoWriteAccess", vars.getLanguage()));
      vars.setMessage(strTabId, myError);
      printPageClosePopUp(response, vars);
      } else {
      printPage(response, vars, strOrgId, strWindowId, strTabId, strFinancialAccountId, null,
      null);
      }*/
      printPage(response, vars, strOrgId, strWindowId, strTabId, strId);

    } else if (vars.commandIn("PROCESS")) {

      String strTabId = "";
      try {
        strTabId = vars.getGlobalVariable("inpTabId", "RefreshListBPFromOA|tabId");
      } catch (Exception e) {
        strTabId = "";
      }
      String strId = "";//vars.getStringParameter("inpFinFinancialAccountId", "");

      vars.setMessage(THIS_CLASS, process(response, vars, strTabId, strId));

      // reload the screen to show the message
      response.sendRedirect(strDireccion + request.getServletPath());

    } else {
      pageError(response);
    }

  }

  private OBError process(HttpServletResponse response, VariablesSecureApp vars, String strTabId,
      String strId) {

    User COOTRACK_USER = OBDal.getInstance().get(User.class, vars.getUser());

    //CootrackHttpClient con = new CootrackHttpClient();
    OpenApiUtils utils = new OpenApiUtils(/*, COOTRACK_USER*/);
    String hasil = "";
    OBError msg = new OBError();
    OBContext.setAdminMode();

    try {
      vars.setSessionValue("statusRetrieveCustomer", "SedangDlmRetrieveData");
      //proses di mulai
      JSONObject jObject = execute(utils);
      hasil = jObject.get("jawaban").toString();
      //            Thread.sleep(6003);

      //simpan perubahan
      OBDal.getInstance().flush();
      OBDal.getInstance().commitAndClose();

    } catch (CustomJsonErrorResponseException jrr) {
      OBDal.getInstance().rollbackAndClose();
      System.out.println("MASUK JSONEXCEPTION");
      vars.removeSessionValue("statusRetrieveCustomer");
      msg.setTitle("Error");
      msg.setType("ERROR");
      msg.setMessage("Error : " + jrr.getMessage());
      return msg;
    } catch (JSONException jre) {
      OBDal.getInstance().rollbackAndClose();
      vars.removeSessionValue("statusRetrieveCustomer");
      msg.setTitle("Error");
      msg.setType("ERROR");
      msg.setMessage("Error : " + jre.getMessage());
      return msg;
    } catch (Exception t) {
      OBDal.getInstance().rollbackAndClose();
      vars.removeSessionValue("statusRetrieveCustomer");
      msg.setTitle("Error");
      msg.setType("ERROR");
      msg.setMessage("Error : " + t.getMessage());
      return msg;
    } catch (Throwable thr) {
      OBDal.getInstance().rollbackAndClose();
      vars.removeSessionValue("statusRetrieveCustomer");
      msg.setTitle("Error");
      msg.setType("ERROR");
      msg.setMessage("Error : " + thr.getMessage());
      return msg;
    } finally {
      vars.removeSessionValue("statusRetrieveCustomer");
      OBContext.restorePreviousMode();
      utils.closeConnection();
    }

    String strMessage = hasil;
    msg.setTitle(Utility.messageBD(this, "Success", vars.getLanguage()));
    msg.setType("Success");
    msg.setMessage(strMessage);
    //
    return msg;

  }

  private void printPage(HttpServletResponse response, VariablesSecureApp vars, String strOrgId,
      String strWindowId, String strTabId, String strId) throws IOException, ServletException {

    log4j.debug("Output: Refresh Business Partner Pressed");

    String dateFormat = OBPropertiesProvider.getInstance().getOpenbravoProperties()
        .getProperty("dateFormat.java");
    SimpleDateFormat dateFormater = new SimpleDateFormat(dateFormat);

    OBContext.setAdminMode();

    ToolBar toolbar = new ToolBar(this, vars.getLanguage(), THIS_CLASS, false, "", "", "", false,
        "ad_forms", strReplaceWith, false, true);
    toolbar.prepareSimpleToolBarTemplate();
    try {
      XmlDocument xmlDocument = xmlEngine.readXmlTemplate(THIS_TEMPLATE).createXmlDocument();
      xmlDocument.setParameter("toolbar", toolbar.toString());

      WindowTabs tabs = new WindowTabs(this, vars, THIS_PACKAGE_CLASS);
      xmlDocument.setParameter("parentTabContainer", tabs.parentTabs());
      xmlDocument.setParameter("mainTabContainer", tabs.mainTabs());
      xmlDocument.setParameter("childTabContainer", tabs.childTabs());
      NavigationBar nav = new NavigationBar(this, vars.getLanguage(), THIS_CLASS + ".html",
          classInfo.id, classInfo.type, strReplaceWith, tabs.breadcrumb());
      xmlDocument.setParameter("navigationBar", nav.toString());
      LeftTabsBar lBar = new LeftTabsBar(this, vars.getLanguage(), THIS_CLASS + ".html",
          strReplaceWith);
      xmlDocument.setParameter("leftTabs", lBar.manualTemplate());

      xmlDocument.setParameter("directory", "var baseDirectory = \"" + strReplaceWith + "/\";\n");
      xmlDocument.setParameter("language", "defaultLang=\"" + vars.getLanguage() + "\";");
      xmlDocument.setParameter("theme", vars.getTheme());

      xmlDocument.setParameter("dateDisplayFormat", vars.getSessionValue("#AD_SqlDateFormat"));
      xmlDocument.setParameter("mainDate", DateTimeData.today(this));
      xmlDocument.setParameter("windowId", strWindowId);
      xmlDocument.setParameter("tabId", strTabId);
      xmlDocument.setParameter("orgId", strOrgId);
      //xmlDocument.setParameter("finFinancialAccountId", strFinancialAccountId);

      OBError myMessage = vars.getMessage(THIS_CLASS);
      vars.removeMessage(THIS_CLASS);
      if (vars.getSessionValue("statusRetrieveCustomer").equals("SedangDlmRetrieveData")) {
        myMessage = new OBError();
        myMessage.setTitle("Warning");
        myMessage.setType("WARNING");
        myMessage
            .setMessage("Previous Process is running in background, please wait until finish !");

        xmlDocument.setParameter("formContent", "hidden");
      }
      if (myMessage != null) {
        xmlDocument.setParameter("messageType", myMessage.getType());
        xmlDocument.setParameter("messageTitle", myMessage.getTitle());
        xmlDocument.setParameter("messageMessage", myMessage.getMessage());
      }

      response.setContentType("text/html; charset=UTF-8");
      PrintWriter out = response.getWriter();
      out.println(xmlDocument.print());
      out.close();
    } catch (Exception e) {
      throw new OBException(e);
    } finally {
      OBContext.restorePreviousMode();
    }
  }

  //method execute
  private JSONObject execute(OpenApiUtils utils)
      throws JSONException, CustomJsonErrorResponseException, Exception, Throwable {
    String hasil = "";

    JSONObject json = new JSONObject();

    if (utils.getCurrentPassword() != null) {
      JSONObject hasilRetrieve = utils.requestListChildAccount(null);
      hasil = hasilRetrieve.get("msg").toString();
      if (hasil.length() == 0 || hasil.toLowerCase().equals("ok")) {
        // Retrieve seluruh Data dari OpenAPi Ke BP
		System.out.println("dikirim ke validateBPList : "+hasilRetrieve.toString());
        new ResponseResultToDB(utils).validateBPList(hasilRetrieve);
      }
      json.put("jawaban", hasil);

    } else {
      throw new Exception("This Openbravo user not synchronized with OpenAPI !");
    }
    // and return it
    return json;

  }

  public String getServletInfo() {
    return "Retrieve BP By Fachmi Rizal";
  } // end of getServletInfo() method

}
