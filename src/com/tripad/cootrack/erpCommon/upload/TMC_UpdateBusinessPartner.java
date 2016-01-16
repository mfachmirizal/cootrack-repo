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
import org.apache.commons.lang.WordUtils;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.idl.proc.Value;
import org.openbravo.model.ad.access.User;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Location;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.model.common.geography.Country;
import org.openbravo.model.financialmgmt.payment.FIN_FinancialAccount;
import org.openbravo.model.financialmgmt.payment.PaymentTerm;
import org.openbravo.model.financialmgmt.payment.FIN_PaymentMethod;
import org.openbravo.model.pricing.pricelist.PriceList;
import org.openbravo.service.db.DalConnectionProvider;
/**
 *
 * @author M Fachmi Rizal@Global Anugerah Indonesia
 */

public class TMC_UpdateBusinessPartner extends IdlServiceJava {
    
    private static Logger log=Logger.getLogger(TMC_UpdateBusinessPartner.class);
    
    BusinessPartner businessPartnerData = null;
    User contactData = null;
    org.openbravo.model.common.geography.Location locationData = null;
    Location locationBpartnerData = null;
    
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
            new Parameter("Kota", Parameter.STRING), //Kota
            new Parameter("Negara", Parameter.STRING), //Negara
            new Parameter("KodePos", Parameter.STRING), //KodePos
            new Parameter("PhoneCompany", Parameter.STRING), //Phone Company
            new Parameter("Fax", Parameter.STRING), //Fax
            new Parameter("PriceList", Parameter.STRING), //Price List
            new Parameter("PaymentMethod", Parameter.STRING),
            new Parameter("PaymentTerm", Parameter.STRING), //Payment Term
            new Parameter("FinancialAccount", Parameter.STRING), //Financial Account
            new Parameter("FirstName", Parameter.STRING), //FirstName
            new Parameter("LastName", Parameter.STRING),//LastName
            new Parameter("Email", Parameter.STRING),
            new Parameter("Phone", Parameter.STRING)
              //,new Parameter("NewContact", Parameter.STRING)
                
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
        validator.checkString(values[7],526); //Phone Company
        validator.checkString(values[8],526); //Fax
        validator.checkString(values[9],526); //PriceList
        validator.checkString(values[10],526); //PatmentTerm
        validator.checkString(values[11],526); //Patment Method
        validator.checkString(values[12],526); //Financial Account
        validator.checkString(values[13],526); //FirstName
        validator.checkString(values[14],526); //LastName
        validator.checkString(values[15],526); //Email
        validator.checkString(values[16],526); //Phone
        //   validator.checkString(values[16],526); //NewContact
        return values;
    }
    
    @Override
    public BaseOBObject internalProcess(Object... values) throws Exception {
        
        return updateDataBPartner((String) values[0], (String) values[1], (String) values[2],
                (String) values[3], (String) values[4], (String) values[5],
                (String) values[6], (String) values[7], (String) values[8],(String) values[9],
                (String) values[10],(String) values[11],(String) values[12],(String) values[13],
                (String) values[14],(String) values[15],(String) values[16]
                //,(String) values[16]
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
            final String paymentmethod,
            final String paymentTerm,
            final String financialAccount,
            final String firstName,
            final String lastName,
            final String email,
            final String phone
    
            
    )
            throws Exception {
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        
        //Customer : C_BPartner - cuma 1 record
        //Location : C_BPartner_Location - multiple
        //Contact  : AD_USER - multiple
        
        final OBCriteria<BusinessPartner> businessPartnerCriteria = OBDal.getInstance()
                .createCriteria(BusinessPartner.class);
        businessPartnerCriteria.add(Restrictions.eq(BusinessPartner.PROPERTY_SEARCHKEY, sKey));
        //businessPartnerCriteria.add(Restrictions.eq(BusinessPartner.PROPERTY_CLIENT, OBContext.getOBContext().getCurrentClient()));
        businessPartnerCriteria.add(Restrictions.eq(BusinessPartner.PROPERTY_CREATEDBY, OBContext.getOBContext().getUser()));
        
        
        if (businessPartnerCriteria.list().isEmpty())  {
            throw new OBException("BusinessPartner Not Found ! ("+sKey+")");
        }
        
        for (BusinessPartner businessPartner : businessPartnerCriteria.list()) {
            businessPartnerData = businessPartner;
            businessPartnerData.setName(commercialName);
            
            //Customer
            if (isEdit(priceList)){
                PriceList priceListExist = findDALInstance(false, PriceList.class, new Value(PriceList.PROPERTY_NAME, priceList));
                if (priceListExist == null) {
                    throw new OBException("Price List  \""+priceList+"\" doesn't exists");
                }
                businessPartnerData.setPriceList(priceListExist);
            } else {
                businessPartnerData.setPriceList(null);
            }

            if (isEdit(paymentmethod)){
                FIN_PaymentMethod paymentmethodExist = findDALInstance(false, FIN_PaymentMethod.class, new Value(FIN_PaymentMethod.PROPERTY_NAME, paymentmethod));
                if (paymentmethodExist == null) {
                    throw new OBException("Payment Method  \""+paymentmethod+"\" doesn't exists");
                }
                businessPartnerData.setPaymentMethod(paymentmethodExist);
            } else {
                businessPartnerData.setPaymentMethod(null);
            }
            
            if (isEdit(paymentTerm)){
                PaymentTerm paymentTermExist = findDALInstance(false, PaymentTerm.class, new Value(PaymentTerm.PROPERTY_SEARCHKEY, paymentTerm));
                if (paymentTermExist == null) {
                    throw new OBException("Payment Term  \""+paymentTerm+"\" doesn't exists");
                }
                businessPartnerData.setPaymentTerms(paymentTermExist);
            } else {
                businessPartnerData.setPaymentTerms(null);
            }
            
            if (isEdit(financialAccount)){
                FIN_FinancialAccount accountExist = findDALInstance(false, FIN_FinancialAccount.class, new Value(FIN_FinancialAccount.PROPERTY_NAME, financialAccount));
                if (accountExist == null) {
                    throw new OBException("Financial Account  \""+financialAccount+"\" doesn't exists");
                }
                businessPartnerData.setAccount(accountExist);
            } else {
                businessPartnerData.setAccount(null);
            }
            
            //Country countryExist = null;
            
            Country countryExist = findDALInstance(false, Country.class, new Value(Country.PROPERTY_NAME, WordUtils.capitalizeFully(negara.trim())));
            if (countryExist == null) {
                throw new OBException("Country  \""+negara.trim()+"\" doesn't exists");
            }
            //}
            
            
            
            OBCriteria<Location> locationBpCrit = OBDal.getInstance()
                    .createCriteria(Location.class);
            locationBpCrit.add(Restrictions.eq(Location.PROPERTY_CREATEDBY, OBContext.getOBContext().getUser()));
            locationBpCrit.add(Restrictions.eq(Location.PROPERTY_NAME, getBPLocationName(kota.trim(),alamat.trim())));
            locationBpCrit.add(Restrictions.eq(Location.PROPERTY_BUSINESSPARTNER, businessPartnerData));
            
            if (locationBpCrit.list().isEmpty()) { //check apakah Bisnis Partner location dengan nama yg sama sudah ada ? samakan yg alama dengan excel : buat baru
                locationBpartnerData = OBProvider.getInstance().get(Location.class);
                locationBpartnerData.setActive(true);
                OBCriteria<org.openbravo.model.common.geography.Location> locationCrit = OBDal.getInstance()
                        .createCriteria(org.openbravo.model.common.geography.Location.class);
                locationCrit.add(Restrictions.eq(org.openbravo.model.common.geography.Location.PROPERTY_CREATEDBY, OBContext.getOBContext().getUser()));
                locationCrit.add(Restrictions.sqlRestriction("UPPER(TRIM(address1)) = UPPER(TRIM('"+alamat.trim()+"'))"));//eq(org.openbravo.model.common.geography.Location.PROPERTY_ADDRESSLINE1, alamat));
                locationCrit.add(Restrictions.sqlRestriction("UPPER(TRIM(city)) = UPPER(TRIM('"+kota.trim()+"'))"));//locationCrit.add(Restrictions.eq(org.openbravo.model.common.geography.Location.PROPERTY_CITYNAME, kota));
                locationCrit.add(Restrictions.sqlRestriction("UPPER(TRIM(postal)) = UPPER(TRIM('"+kodePos.trim()+"'))"));
                locationCrit.add(Restrictions.eq(org.openbravo.model.common.geography.Location.PROPERTY_COUNTRY, countryExist));
                if (locationCrit.list().isEmpty()) { //check apakah Location dengan alamat di atas sudah ada ? : update yg lama, samakan dengan excel : buat baru
                    locationData = OBProvider.getInstance().get(org.openbravo.model.common.geography.Location.class);
                    locationData.setActive(true);
                    locationData.setAddressLine1(alamat.trim());
                    locationData.setCityName(kota.trim());
                    locationData.setCountry(countryExist);
                    locationData.setPostalCode(kodePos.trim());
                    OBDal.getInstance().save(locationData);
                } else {
                    locationData = locationCrit.list().get(0);
                    locationData.setAddressLine1(alamat.trim());
                    locationData.setCityName(kota.trim());
                    locationData.setCountry(countryExist);
                    locationData.setPostalCode(kodePos.trim());
                }
                if (locationData != null) {
                    locationBpartnerData.setLocationAddress(locationData);
                } else {
                    throw new OBException("Error : locationData variable is null");
                }
                locationBpartnerData.setBusinessPartner(businessPartnerData);
                locationBpartnerData.setName(getBPLocationName(kota.trim(),alamat.trim()));
                locationBpartnerData.setPhone(phoneCompany);
                locationBpartnerData.setFax(fax);
                OBDal.getInstance().save(locationBpartnerData);
            } else {
                for (Location locBPExisting : locationBpCrit.list()) {
                    
                    //for (org.openbravo.model.common.geography.Location locExisting : locBPExisting.getLocationAddress().getBusinessPartnerLocationList()) {
                    locBPExisting.getLocationAddress().setAddressLine1(alamat.trim());
                    locBPExisting.getLocationAddress().setCityName(kota.trim());
                    locBPExisting.getLocationAddress().setCountry(countryExist);
                    locBPExisting.getLocationAddress().setPostalCode(kodePos.trim());
                    
                    //locBPExisting.setBusinessPartner(businessPartnerData);
                    locBPExisting.setName(getBPLocationName(kota.trim(),alamat.trim()));
                    locBPExisting.setPhone(phoneCompany);
                    locBPExisting.setFax(fax);
                    OBDal.getInstance().save(locBPExisting);
                    //}
                }
            }
            
            //Contact
            OBCriteria<User> contactCrit = OBDal.getInstance()
                    .createCriteria(User.class);
            contactCrit.add(Restrictions.eq(User.PROPERTY_CREATEDBY, OBContext.getOBContext().getUser()));
            contactCrit.add(Restrictions.eq(User.PROPERTY_BUSINESSPARTNER, businessPartnerData));
            contactCrit.add(Restrictions.sqlRestriction("UPPER(TRIM(firstname)) = UPPER(TRIM('"+firstName.trim()+"'))"));
            contactCrit.add(Restrictions.sqlRestriction("UPPER(TRIM(lastname)) = UPPER(TRIM('"+lastName.trim()+"'))"));
            contactCrit.add(Restrictions.sqlRestriction("UPPER(TRIM(email)) = UPPER(TRIM('"+email.trim()+"'))"));
            contactCrit.add(Restrictions.sqlRestriction("UPPER(TRIM(phone)) = UPPER(TRIM('"+phone.trim()+"'))"));
            
            if (contactCrit.list().isEmpty()) { //bila kosong / blm ada sebelumnya ? buat baru : edit yg lama, sesuaikan dengan excel
                contactData = OBProvider.getInstance().get(User.class);
                contactData.setActive(true);
                contactData.setFirstName(firstName.trim());
                contactData.setLastName(lastName.trim());
                contactData.setEmail(email.trim());
                contactData.setPhone(phone.trim());
                contactData.setName(firstName.trim()+" "+lastName.trim());
                contactData.setBusinessPartner(businessPartnerData);
                OBDal.getInstance().save(contactData);
            } else {
                for (User existingUser : contactCrit.list()) {
                    existingUser.setFirstName(firstName.trim());
                    existingUser.setLastName(lastName.trim());
                    existingUser.setEmail(email.trim());
                    existingUser.setPhone(phone.trim());
                    existingUser.setName(firstName.trim()+" "+lastName.trim());
                    OBDal.getInstance().save(existingUser);
                }
            }
            
            
            //sHRLeaveEncash = OBProvider.getInstance().get(SHRLeaveEncash.class);
            //sHRLeaveEncash.setActive(true);
            
            OBDal.getInstance().save(businessPartnerData);
            OBDal.getInstance().flush();
        }
        
        return businessPartnerData;
    }
    
    private boolean isEdit(String param) {
        if (param != null) {
            if (!param.equals("")) {
                return true;
            }
        }
        return false;
    }
    
    
    private String getBPLocationName(String city,String addr1) {
        return city+", "+addr1;
    }
}
