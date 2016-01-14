/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.tripad.cootrack.erpCommon.upload;

import org.openbravo.idl.proc.Parameter;
import org.openbravo.idl.proc.Validator;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.*;

import org.openbravo.base.exception.OBException;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.base.structure.BaseOBObject;
import org.openbravo.dal.service.OBDal;
import org.openbravo.erpCommon.utility.Utility;
import org.openbravo.module.idljava.proc.IdlServiceJava;
import org.openbravo.dal.core.OBContext;
import java.math.BigDecimal;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.idl.proc.Value;
import org.openbravo.model.ad.access.User;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Location;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.financialmgmt.payment.FIN_FinancialAccount;
import org.openbravo.model.financialmgmt.payment.PaymentTerm;
import org.openbravo.model.pricing.pricelist.PriceList;
import org.openbravo.service.db.DalConnectionProvider;
/**
 *
 * @author M Fachmi Rizal@Global Anugerah Indonesia
 */

public class TMC_UpdateBusinessPartner extends IdlServiceJava {
    
    private static Logger log=Logger.getLogger(TMC_UpdateBusinessPartner.class);
    
    BusinessPartner businessPartnerData = null;
    User dataUser = null;
    Location locationData = null;
    
    @Override
    public String getEntityName() {
        return "Update Business Partner Entity";
    }
    
    @Override
    public Parameter[] getParameters() {
        return new Parameter[] {
            new Parameter("Organization", Parameter.STRING),
            new Parameter("SearchKey", Parameter.STRING), //Search Key BP
            new Parameter("CommercialName", Parameter.STRING), // Nama
            new Parameter("Alamat", Parameter.STRING), //
            new Parameter("Kota", Parameter.STRING), //Ketersediaan cuti
            new Parameter("Negara", Parameter.STRING), //Gaji Pokok
            new Parameter("KodePos", Parameter.STRING), //Tunjangan Pasutri
            new Parameter("PhoneCompany", Parameter.STRING), //Tunjangan Anak
            new Parameter("Fax", Parameter.STRING), //Tunjangan Operasional
            new Parameter("PriceList", Parameter.STRING), //Tunjangan Jabtan
            new Parameter("PatmentTerm", Parameter.STRING), //PPH
            new Parameter("FinancialAccount", Parameter.STRING), //tkb
            new Parameter("FirstName", Parameter.STRING), //PembayaranCuti
            new Parameter("LastName", Parameter.STRING),
            new Parameter("Email", Parameter.STRING),
            new Parameter("Phone", Parameter.STRING),
            new Parameter("NewContact", Parameter.STRING)
                
        };
    }
    
    @Override
    protected Object[] validateProcess(Validator validator, String... values) throws Exception {
        validator.checkOrganization(values[0]);
        validator.checkNotNull(validator.checkString(values[1], 60)); //SearchKey
        validator.checkString(values[2],225); //CommercialName
        validator.checkString(values[3],526); //Alamat
        validator.checkString(values[4],526); //Kota
        validator.checkString(values[5],526); //Negara
        validator.checkString(values[6],526); //KodePos
        validator.checkString(values[7],526); //PhoneCompany
        validator.checkString(values[8],526); //Fax
        validator.checkString(values[9],526); //PriceList
        validator.checkString(values[10],526); //PatmentTerm
        validator.checkString(values[11],526); //FinancialAccount
        validator.checkString(values[12],526); //FirstName
        validator.checkString(values[13],526); //LastName
        validator.checkString(values[14],526); //Email
        validator.checkString(values[15],526); //Phone
        validator.checkString(values[16],526); //NewContact
        return values;
    }
    
    @Override
    public BaseOBObject internalProcess(Object... values) throws Exception {
        
        return updateDataBPartner((String) values[0], (String) values[1], (String) values[2],
                (String) values[3], (String) values[4], (String) values[5],
                (String) values[6], (String) values[7], (String) values[8],(String) values[9],
                (String) values[10],(String) values[11],(String) values[12],(String) values[13],
                (String) values[14],(String) values[15],(String) values[16]
        );
    }
    
    public BaseOBObject updateDataBPartner(
            final String organization, //rowOrganization
            final String sKey,
            final String commercialName,
            final String alamat,
            final String kota,
            final String negara,
            final String kodePos,
            final String phoneCompany,
            final String fax,
            final String priceList,
            final String paymentTerm,
            final String financialAccount,
            final String firstName,
            final String lastName,
            final String email,
            final String phone,
            final String newContact
            
    )
            throws Exception {
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        
        //Customer : C_BPartner - cuma 1 record
        //Location : C_BPartner_Location - multiple
        //Contact  : AD_USER - multiple
        
        final OBCriteria<BusinessPartner> businessPartnerCriteria = OBDal.getInstance()
                .createCriteria(BusinessPartner.class);
        businessPartnerCriteria.add(Restrictions.eq(BusinessPartner.PROPERTY_SEARCHKEY, sKey));
        businessPartnerCriteria.add(Restrictions.eq(BusinessPartner.PROPERTY_CLIENT, OBContext.getOBContext().getCurrentClient()));
        
        if (businessPartnerCriteria.list().isEmpty())  {
            throw new OBException("BusinessPartner Not Found ! ("+sKey+")");
        }
        
        String docno ="";
        for (BusinessPartner businessPartner : businessPartnerCriteria.list()) {
            businessPartnerData = businessPartner;
            businessPartnerData.setName(commercialName);
            
            //Customer
            PriceList priceListExist = findDALInstance(false, PriceList.class, new Value(PriceList.PROPERTY_NAME, priceList));
            if (priceListExist == null) {
                throw new OBException("Price List  \""+priceList+"\" doesn't exists");
            }
            businessPartnerData.setPriceList(priceListExist);
            
            PaymentTerm paymentTermExist = findDALInstance(false, PaymentTerm.class, new Value(PaymentTerm.PROPERTY_SEARCHKEY, paymentTerm));
            if (paymentTermExist == null) {
                throw new OBException("Payment Term  \""+paymentTerm+"\" doesn't exists");
            }
            businessPartnerData.setPaymentTerms(paymentTermExist);
            
            FIN_FinancialAccount accountExist = findDALInstance(false, FIN_FinancialAccount.class, new Value(FIN_FinancialAccount.PROPERTY_NAME, financialAccount));
            if (accountExist == null) {
                throw new OBException("Financial Account  \""+financialAccount+"\" doesn't exists");
            }
            businessPartnerData.setAccount(accountExist);
            
            
            //sHRLeaveEncash = OBProvider.getInstance().get(SHRLeaveEncash.class);
            //sHRLeaveEncash.setActive(true);
            
            OBDal.getInstance().save(businessPartnerData);
            OBDal.getInstance().flush();
        }
        
        return businessPartnerData;
    }
}
