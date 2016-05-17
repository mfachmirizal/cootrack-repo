/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.erpCommon.process;

/**
 *
 * @author Tias Ade Putra
 */

import com.tripad.cootrack.data.TmcDocumentUpdateLine;
import java.math.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;
import org.openbravo.client.application.ApplicationConstants;
import org.openbravo.client.application.process.BaseProcessActionHandler;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.service.db.CallStoredProcedure;
import org.openbravo.erpCommon.utility.OBMessageUtils;
import org.openbravo.base.exception.OBException;
import org.openbravo.base.provider.OBProvider;

import org.openbravo.model.common.order.Order;
import org.openbravo.model.common.order.OrderLine;
import org.openbravo.model.common.plm.Product;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Location;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.financialmgmt.payment.FIN_PaymentMethod;
import org.openbravo.model.common.enterprise.DocumentType;
import org.openbravo.model.ad.utility.Sequence;
import org.openbravo.model.common.enterprise.Warehouse;
import org.openbravo.model.financialmgmt.payment.PaymentTerm;
import org.openbravo.model.pricing.pricelist.PriceList;
import org.openbravo.model.common.currency.Currency;
import org.openbravo.model.financialmgmt.tax.TaxRate;
import org.openbravo.model.financialmgmt.payment.FIN_PaymentSchedule;
/**
 * @author Tias Ade Putra
 *
 */
public class CreateOrderByMaintenance extends BaseProcessActionHandler {

    private static final Logger log = Logger.getLogger(CreateOrderByMaintenance.class);

    @Override
    protected JSONObject doExecute(Map<String, Object> parameters, String content) {
        JSONObject result = new JSONObject();
        try {
            JSONObject request = new JSONObject(content);
            JSONObject params = request.getJSONObject("_params");



            String tmcDocumentUpdateLineID = "";  
            String strLocationID;
            Date orderDate = Calendar.getInstance().getTime();




            BigDecimal nominalPulsa = BigDecimal.ZERO;
            BigDecimal nominalQuota = BigDecimal.ZERO;
            BigDecimal profitPulsa = BigDecimal.ZERO;
            BigDecimal profitQuota = BigDecimal.ZERO;
            BigDecimal totalOrder = BigDecimal.ZERO;
              
            TmcDocumentUpdateLine parentDocumentUpdateLine = null;
            Order parentOrder = null;
            OrderLine parentOrderLine = null;
            Organization parentOrg = null;
            Product parentProduct = null;
            BusinessPartner parentBussinesPartner = null;
            DocumentType parentDocumentType = null;
            Location parentLocation = null;
            Warehouse parentWarehouse = null;
            Sequence parentSequence = null;
            PriceList parentPriceList = null;
            FIN_PaymentMethod parentFIN_PaymentMethod = null;
            PaymentTerm parentPaymentTerm = null;
            Currency parentCurrency = null;
            TaxRate parentTax = null;

            if (request.has("TMC_Documentupdateline_ID")) {
        tmcDocumentUpdateLineID = request.getString("TMC_Documentupdateline_ID");
        parentDocumentUpdateLine = OBDal.getInstance().get(TmcDocumentUpdateLine.class, tmcDocumentUpdateLineID);
        parentOrg = OBDal.getInstance().get(Organization.class, parentDocumentUpdateLine.getOrganization().getId());
        parentBussinesPartner = OBDal.getInstance().get(BusinessPartner.class, parentDocumentUpdateLine.getCustomerName().getId());
        List<Location> obloc = parentBussinesPartner.getBusinessPartnerLocationList();
        parentLocation = OBDal.getInstance().get(Location.class, obloc.get(0).getId());

        nominalPulsa = parentDocumentUpdateLine.getNOMIsiPulsaReg();
        nominalQuota = parentDocumentUpdateLine.getNOMIsiPulsaQuota();
        profitPulsa = parentDocumentUpdateLine.getProfitPulsa();
        profitQuota = parentDocumentUpdateLine.getProfitQuota();

        }

       String strDocumentTypeID = params.getString("C_Doctype_ID");
       parentDocumentType =  OBDal.getInstance().get(DocumentType.class, strDocumentTypeID);
       String strPriceListID = params.getString("M_PriceList_ID");
       parentPriceList =  OBDal.getInstance().get(PriceList.class, strPriceListID);
       String strPaymentMethodID = params.getString("FIN_Paymentmethod_ID");
       parentFIN_PaymentMethod =  OBDal.getInstance().get(FIN_PaymentMethod.class, strPaymentMethodID);
       String strPaymentTermsID = params.getString("C_PaymentTerm_ID");
       parentPaymentTerm =  OBDal.getInstance().get(PaymentTerm.class, strPaymentTermsID);
       String strWarehouseID = params.getString("M_Warehouse_ID");
       parentWarehouse = OBDal.getInstance().get(Warehouse.class, strWarehouseID);
       String strProductID = params.getString("M_Product_ID");
       parentProduct = OBDal.getInstance().get(Product.class, strProductID);
       String strTaxID = params.getString("C_Tax_ID");
       parentTax = OBDal.getInstance().get(TaxRate.class, strTaxID);

    
       parentCurrency =  OBDal.getInstance().get(Currency.class, parentPriceList.getCurrency().getId());
       String strDocumentNo = parentDocumentType.getDocumentSequence().getNextAssignedNumber().toString();
       String strfREFIKDocumentNo = parentDocumentType.getDocumentSequence().getPrefix().toString();
       String documentNo = strfREFIKDocumentNo+strDocumentNo;
       
       Order orderInsert = OBProvider.getInstance().get(Order.class);

       orderInsert.setActive(true);
       orderInsert.setOrganization(parentOrg);
       orderInsert.setSalesTransaction(true);
       orderInsert.setBusinessPartner(parentBussinesPartner);
       orderInsert.setPartnerAddress(parentLocation);
       orderInsert.setCurrency(parentCurrency);
       orderInsert.setDocumentNo(documentNo);
       orderInsert.setDocumentType(parentDocumentType);
       orderInsert.setTransactionDocument(parentDocumentType);
       orderInsert.setOrderDate(orderDate);
       orderInsert.setAccountingDate(orderDate);
       orderInsert.setScheduledDeliveryDate(orderDate);
       orderInsert.setInvoiceTerms("I");
       orderInsert.setWarehouse(parentWarehouse);
       orderInsert.setPriceList(parentPriceList);
       orderInsert.setPaymentMethod(parentFIN_PaymentMethod);
       orderInsert.setPaymentTerms(parentPaymentTerm);


       OBDal.getInstance().save(orderInsert);
       OBDal.getInstance().flush();

       Long seqno =  parentDocumentType.getDocumentSequence().getIncrementBy()  + parentDocumentType.getDocumentSequence().getNextAssignedNumber();

       Sequence documentSequenceupdate = OBDal.getInstance().get(Sequence.class,parentDocumentType.getDocumentSequence().getId());
       documentSequenceupdate.setNextAssignedNumber(seqno);
       OBDal.getInstance().save(documentSequenceupdate);
       OBDal.getInstance().flush();

       totalOrder = (nominalPulsa.add(nominalQuota)).add((profitPulsa).add(profitQuota));

        OrderLine orderLineInsert = OBProvider.getInstance().get(OrderLine.class);

       orderLineInsert.setActive(true);
       orderLineInsert.setOrganization(parentOrg);
       orderLineInsert.setSalesOrder(orderInsert);
       orderLineInsert.setProduct(parentProduct);
       orderLineInsert.setCurrency(parentCurrency);
       orderLineInsert.setWarehouse(parentWarehouse);
       orderLineInsert.setUOM(parentProduct.getUOM());
       orderLineInsert.setOrderDate(orderDate);
       orderLineInsert.setLineNo(new Long(10));
       orderLineInsert.setLineNetAmount(totalOrder);
       orderLineInsert.setOrderedQuantity(new BigDecimal("1"));
       orderLineInsert.setReservedQuantity(new BigDecimal("1"));
       orderLineInsert.setListPrice(totalOrder);
       orderLineInsert.setUnitPrice(totalOrder);
       orderLineInsert.setPriceLimit(totalOrder);
       orderLineInsert.setTax(parentTax);

       OBDal.getInstance().save(orderLineInsert);
       OBDal.getInstance().flush();


         Long temsdate = parentPaymentTerm.getOverduePaymentDaysRule();

          Date dateInstance = new Date();
          Calendar cal = Calendar.getInstance();
          cal.setTime(dateInstance);
          cal.add(Calendar.DATE, temsdate.intValue());
          Date duedate = cal.getTime();

       FIN_PaymentSchedule orderSchedule = OBProvider.getInstance().get(FIN_PaymentSchedule.class);
       
       orderSchedule.setActive(true);
       orderSchedule.setOrganization(parentOrg);
       orderSchedule.setOrigDueDate(orderDate);
       orderSchedule.setDueDate(duedate);
       orderSchedule.setOrigDueDate(duedate);
       orderSchedule.setFinPaymentmethod(parentFIN_PaymentMethod);
       orderSchedule.setOrder(orderInsert);
       orderSchedule.setCurrency(parentCurrency);
       orderSchedule.setAmount(totalOrder);
       orderSchedule.setOutstandingAmount(totalOrder);
       orderSchedule.setUpdatePaymentPlan(true);
       orderSchedule.setExpectedDate(orderDate);
       
       OBDal.getInstance().save(orderSchedule);
       OBDal.getInstance().flush();

      
       TmcDocumentUpdateLine tmcforupdate = OBDal.getInstance().get(TmcDocumentUpdateLine.class,parentDocumentUpdateLine.getId());
       tmcforupdate.setProcess(true);
       tmcforupdate.setSalesorder(true);
       
       OBDal.getInstance().save(tmcforupdate);
       OBDal.getInstance().flush();


       List<Object> parameter = new ArrayList<Object>();
       parameter.add(orderInsert.getId());

       CallStoredProcedure.getInstance().call("tmc_order_post", parameter, null, true, false);
  
       OBDal.getInstance().flush();
       OBDal.getInstance().commitAndClose();

             JSONArray actions = new JSONArray();

      String pesan ="Process completed successfully";

      JSONObject msg = new JSONObject();
      msg.put("msgType", "success");
      // XXX: these two messages should be translatable, like OBEXAPP_MinGtMax above
      msg.put("msgTitle", "Success");
      msg.put("msgText", pesan);

      JSONObject msgTotalAction = new JSONObject();
      msgTotalAction.put("showMsgInProcessView", msg);
      actions.put(msgTotalAction);
      result.put("responseActions", actions);
        
        return result;
        } catch (JSONException e) {
           return new JSONObject();
        } catch(Throwable t){
        return errorMessage(result,t.getMessage());
    }
   }

private JSONObject errorMessage(JSONObject result, String msgParam) {
    try {
      JSONArray actions = new JSONArray();

      String pesan ="Error : "+msgParam;

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
