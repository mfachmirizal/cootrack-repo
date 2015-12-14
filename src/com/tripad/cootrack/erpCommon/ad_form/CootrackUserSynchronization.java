/*
2  *************************************************************************
3  * The contents of this file are subject to the Openbravo  Public  License
4  * Version  1.0  (the  "License"),  being   the  Mozilla   Public  License
5  * Version 1.1  with a permitted attribution clause; you may not  use this
6  * file except in compliance with the License. You  may  obtain  a copy of
7  * the License at http://www.openbravo.com/legal/license.html
8  * Software distributed under the License  is  distributed  on  an "AS IS"
9  * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
10  * License for the specific  language  governing  rights  and  limitations
11  * under the License.
12  * The Original Code is Openbravo ERP.
13  * The Initial Developer of the Original Code is Openbravo SL
14  * All portions are Copyright (C) 2001-2008 Openbravo SL
15  * All Rights Reserved.
16  * Contributor(s):  ______________________________________.
17  ************************************************************************
18  */
package com.tripad.cootrack.erpCommon.ad_form;

/**
 * fooPackage <-
 * fooNameClass
 * fooEntitas <-
 * fooPackageNameClass <-
 * fooPackageGaring <-
 **/

import com.tripad.cootrack.data.TmcToken;
import com.tripad.cootrack.data.TmcUserSync;
import com.tripad.cootrack.utility.OpenApiUtils;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.criterion.Restrictions;

import static org.openbravo.base.HttpBaseServlet.strDireccion;
import org.openbravo.base.provider.OBProvider;

import org.openbravo.base.secureApp.HttpSecureAppServlet;
import org.openbravo.base.secureApp.VariablesSecureApp;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.businessUtility.WindowTabs;
import org.openbravo.erpCommon.utility.LeftTabsBar;
import org.openbravo.erpCommon.utility.NavigationBar;
import org.openbravo.erpCommon.utility.OBError;
import org.openbravo.erpCommon.utility.ToolBar;
import org.openbravo.xmlEngine.XmlDocument;
//entitas
//fooEntitas

public class CootrackUserSynchronization extends HttpSecureAppServlet {
    private static final long serialVersionUID = 1L;
    
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {
        VariablesSecureApp vars = new VariablesSecureApp(request);
        
        if (vars.commandIn("DEFAULT")) {
            String strWindowId = vars.getStringParameter("inpwindowId");
            String strTabId = vars.getStringParameter("inpIdTab");
            //param
            //String strUserID = vars.getStringParameter("inpUserId");
            //String strUser = vars.getStringParameter("inpUser");
            String strUserID = OBContext.getOBContext().getUser().getId();
            String strUser = OBContext.getOBContext().getUser().getUsername();
            
            String strPassword = vars.getStringParameter("inpPasswd");
            
            TmcUserSync tmcuserSync = getCurrentUserSync();
            
            String strStatusText = "";
            if (tmcuserSync != null) {
                strStatusText = "This User is synchronized ! <p style=\"color:black\"> [last synchronized : "+tmcuserSync.getUpdated()+"] </p>";
            } else {
                strStatusText = "This User is not synchronized ! ";
            }
            
            printPageDataSheet(response, vars,strWindowId,strTabId,strUserID,strUser,strPassword,strStatusText);
            
        } else if (vars.commandIn("OK")) {
            String strWindowId = vars.getStringParameter("inpwindowId");
            String strTabId = vars.getStringParameter("inpIdTab");
            //param
            String strUserID = vars.getStringParameter("inpUserId");
            String strUser = vars.getStringParameter("inpUser");
            String strPassword = vars.getStringParameter("inpPasswd");
            
            vars.setMessage("CootrackUserSynchronization", process( response, vars,strWindowId,strTabId,strUserID,strUser,strPassword));
            
            response.sendRedirect(strDireccion + request.getServletPath());
            
        } else
            pageError(response);
    }
    
    private OBError process(HttpServletResponse response,
            VariablesSecureApp vars,String strWindowId, String strTabId,String strUserID,String strUser,String strPassword) throws IOException, ServletException {
        
        OBError myMessage = new OBError();
        OpenApiUtils ou = new OpenApiUtils();
        
        
        
        
        //1. delete dlu token yg skrg
        ou.deleteToken();
        
        //2. delete record tmcUserSync untuk user ini
        ou.deleteCurrentUserSync();
        
        //3. insert record tmcUserSync yg baru dari form | jangan lupa, password di encrypt md5
        TmcUserSync newTmcUserSync = OBProvider.getInstance().get(TmcUserSync.class);
        newTmcUserSync.setActive(true);
        newTmcUserSync.setUser(OBContext.getOBContext().getUser());
        newTmcUserSync.setPassword(ou.convertToMd5(strPassword));
        newTmcUserSync.setBTNProcess(false);
        
        OBDal.getInstance().save(newTmcUserSync);
        OBDal.getInstance().flush();
        OBDal.getInstance().commitAndClose();
        
        //4. dapatkan token ke OpenAPi
        TmcToken token = ou.getToken();
        if (token.getReturnCode().equals("20001") || token.getReturnCode().equals("20004")) {
            ou.deleteCurrentUserSync();
            
            myMessage.setTitle("Error");
            myMessage.setType("ERROR");
            myMessage.setMessage("Wrong Username / Password !");
            return myMessage;
        }
        
        /*
        // BEGIN check for missing parameters and return in error in the case
        if (strMProductId == null || strMProductId.equals("")) {
        myMessage.setTitle("Error");
        myMessage.setType("ERROR");
        myMessage.setMessage(Utility.messageBD(this, "HT_NoProductsSelected", vars.getLanguage()));
        return myMessage;
        }
        if (strNewProductCategory == null || strNewProductCategory.equals("")) {
        myMessage.setTitle("Error");
        myMessage.setType("ERROR");
        myMessage.setMessage(Utility.messageBD(this, "HT_NoNewProductCategorySelected", vars
        .getLanguage()));
        return myMessage;
        }
        // END check for missing parameters and return in error in the case
        
        try {
        // peform the actual processing - setting the new product category for the selected products
        intUpdatedProducts = BatchSetProductCategoryData.process(this, strNewProductCategory,
        strMProductId);
        } catch (Exception e) {
        myMessage.setTitle("Error");
        myMessage.setType("ERROR");
        myMessage.setMessage(Utility.messageBD(this, "HT_NewProductCategorySetError", vars
        .getLanguage())
        + e.toString());
        return myMessage;
        }
        */
        myMessage.setTitle("Success");
        myMessage.setType("SUCCESS");
        myMessage.setMessage("User Synchronization Successfully !");
        return myMessage;
    }
    
    /*void process(HttpServletRequest request, HttpServletResponse response,
    VariablesSecureApp vars,String strWindowId, String strTabId,String strUserID,String strUser,String strPassword) throws IOException, ServletException {
    if (log4j.isDebugEnabled())
    log4j.debug("Output: dataSheet");
    
    OBError myMessage = new OBError();
    OBContext.setAdminMode();
    vars.setMessage("CootrackUserSynchronization", null);
    
    /*
    String strErrorMsg = "";
    OBError myMessage = new OBError();
    OBContext.setAdminMode();
    strErrorMsg = myMessage.getMessage();
    
    String[] listId = convertToArray(strID);
    
    String dbgText = "("+listId.length+") "+"ID : "+strID+" <> strTab : "+strTabId+" <> strWindow "+strWindowId+
    " <> strNoSuratIjinBelajar : "+strNoSuratIjinBelajar+" <> strTglSuratIjinBelajar : "+strTglSuratIjinBelajar+" <> strNomorSK:"+strNomorSK+" <> strTglSk :"+strTglSk;
    if (false) {
    throw new OBException(dbgText);
    }
    
    //loop untuk menjalankan procedure tiap id yg di kirim
    int count = 0;
    for (String stringid : listId) {
    try {
    List<Object> params = new ArrayList<Object>();
    params.add(stringid);
    params.add((String) DalUtil.getId(OBContext.getOBContext().getUser()));
    /*if(strTabId.equals("6344C616BEF849EE8551DA1E69FDA2BE")){
    params.add(strNoSuratIjinBelajar);
    params.add(strTglSuratIjinBelajar);
    }
    else {
    
    params.add("");
    params.add("");
    //}
    params.add(strNomorSK);
    params.add(strTglSk);
    //params.add(strSenderCat);
    
    if(strTabId.equals("6344C616BEF849EE8551DA1E69FDA2BE")){
    
    CallStoredProcedure.getInstance().call("gib_tugas_proses_java", params, null, true, false);
    
    }else{
    
    CallStoredProcedure.getInstance().call("gib_ijin_proses_java", params, null, true, false);
    }
    //log4j.debug("lewat proses, tab : "+strTabId+" id : "+strID+" : "+(String) DalUtil.getId(OBContext.getOBContext().getUser())+"() ["+strDebug+"] = "+strGLItemId);
    //log4j.debug("ID ke["+count+"] : "+stringid+"");
    count=count+1;
    
    } catch (Exception e) {
    //OBDal.getInstance().rollbackAndClose();
    log4j.info(e.getMessage());
    myMessage = Utility.translateError(this, vars, vars.getLanguage(), e.getMessage());
    throw new IllegalStateException(e);
    }
    }
    //end loop untuk menjalankan procedure tiap id yg di kirim
    
    
    //OBDal.getInstance().commitAndClose();
    
    myMessage.setTitle("Success");
    myMessage.setType("SUCCESS");
    myMessage.setMessage(count+" List telah di proses");
    
    vars.setMessage(strTabId, myMessage); //D4EA5D16042D4E94889BCCDBB87F787A
    
    printPageClosePopUpAndRefreshParent(response, vars);
    
    }*/
    
    void printPageDataSheet(HttpServletResponse response, VariablesSecureApp vars,
            String strWindowId,String strTabId,String strUserID,String strUser,String strPassword,String strStatusText) throws IOException, ServletException {
        
        if (log4j.isDebugEnabled())
            log4j.debug("Output: dataSheet");
        
        XmlDocument xmlDocument = null;
        xmlDocument = xmlEngine.readXmlTemplate(
                "com/tripad/cootrack/erpCommon/ad_form/CootrackUserSynchronization").createXmlDocument();
        ToolBar toolbar = new ToolBar(this, vars.getLanguage(),
                "CootrackUserSynchronization", false, "", "", "", false, "ad_reports",
                strReplaceWith, false, true);
        toolbar.prepareSimpleToolBarTemplate();
        xmlDocument.setParameter("toolbar", toolbar.toString());
        try {
            WindowTabs tabs = new WindowTabs(this, vars,
                    "com.tripad.cootrack.erpCommon.ad_form.CootrackUserSynchronization");
            xmlDocument.setParameter("parentTabContainer", tabs.parentTabs());
            xmlDocument.setParameter("mainTabContainer", tabs.mainTabs());
            xmlDocument.setParameter("childTabContainer", tabs.childTabs());
            xmlDocument.setParameter("theme", vars.getTheme());
            NavigationBar nav = new NavigationBar(this, vars.getLanguage(),
                    "CootrackUserSynchronization.html", classInfo.id, classInfo.type, strReplaceWith, tabs
                            .breadcrumb());
            xmlDocument.setParameter("navigationBar", nav.toString());
            LeftTabsBar lBar = new LeftTabsBar(this, vars.getLanguage(), "CootrackUserSynchronization.html",
                    strReplaceWith);
            xmlDocument.setParameter("leftTabs", lBar.manualTemplate());
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
        {
            OBError myMessage = vars.getMessage("CootrackUserSynchronization");
            vars.removeMessage("CootrackUserSynchronization");
            if (myMessage != null) {
                xmlDocument.setParameter("messageType", myMessage.getType());
                xmlDocument.setParameter("messageTitle", myMessage.getTitle());
                xmlDocument.setParameter("messageMessage", myMessage.getMessage());
            }
        }
        
        xmlDocument.setParameter("dateDisplayFormat", vars.getSessionValue("#AD_SqlDateFormat"));
        xmlDocument.setParameter("paramLanguage", "defaultLang=\"" + vars.getLanguage() + "\";");
        xmlDocument.setParameter("calendar", vars.getLanguage().substring(0, 2));
        xmlDocument.setParameter("directory", "var baseDirectory = \"" + strReplaceWith + "/\";\n");
        xmlDocument.setParameter("title", "Openbravo & Cootrack Synchronization");
        xmlDocument.setParameter("userID", strUserID);
        xmlDocument.setParameter("user", strUser);
        xmlDocument.setParameter("tabId", strTabId);
        xmlDocument.setParameter("statustext",strStatusText);
        if (strStatusText.equals("This User is not synchronized ! ")) {
            xmlDocument.setParameter("statustextstyle","red");
        } else {
            xmlDocument.setParameter("statustextstyle","green");
        }
        
        
        
        
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(xmlDocument.print());
        out.close();
    }
    
    //method custom
    protected String[] convertToArray(String param) {
        String delims = ",";
        //int index = 0;
        String[] tokens = param.split(delims);
        
        return tokens;
    }
    
    private TmcUserSync getCurrentUserSync(){
        OBCriteria<TmcUserSync> tmcUserSync = OBDal.getInstance().createCriteria(TmcUserSync.class);
        tmcUserSync.add(Restrictions.eq(TmcUserSync.PROPERTY_USER, OBContext.getOBContext().getUser()));
        
        if (tmcUserSync.count() > 0) {
            return tmcUserSync.list().get(0);
        }
        
        return null;
    }
    
    public String getServletInfo() {
        return "Servlet CootrackUserSynchronization. This Servlet was made by Moch Fachmi Rizal";
    } // end of getServletInfo() method
}
