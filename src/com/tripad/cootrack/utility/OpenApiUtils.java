package com.tripad.cootrack.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

 
 import org.openbravo.base.exception.OBException;
 //import org.openbravo.client.kernel.BaseActionHandler;
 import org.openbravo.dal.service.OBDal;
 import com.tripad.cootrack.data.TmcToken;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.dal.service.OBCriteria;
import org.hibernate.criterion.Restrictions;
import org.openbravo.base.provider.OBProvider;

public class OpenApiUtils {
  // Nanti static var ini hapus
  public static final String COOTRACK_USERNAME = "enduserdahlia";
  public static final String COOTRACK_PASSWORD = "123456";
  // static var
  public static final String COOTRACK_GET_TOKEN_URL = "http://api.gpsoo.net/1/auth/access_token?";
  public static final String COOTRACK_GET_MONITOR_URL = "http://api.gpsoo.net/1/account/monitor?"; 

  public OpenApiUtils() {
  }
  
  private String commonParameters(boolean tokenIsAlreadyExist) {
      if (tokenIsAlreadyExist) {
        return "access_token="+getToken()+"&account=" + COOTRACK_USERNAME + "&time="+getUnixTime();
      }else {
        return "account=" + COOTRACK_USERNAME + "&time="+getUnixTime() + "&signature="+ (convertToMd5((convertToMd5(COOTRACK_PASSWORD)) + getUnixTime()));
      }
  }
  
  /**
   * method untuk memeriksa hasil response dari api server
   * @param response berupa message dari server :
   *    Code 	Desc
   *     10001 	System Error
   *     10002 	The API you request does not exist
   *     10003 	Request frequence exceeds limitation
   *     10004 	access_token not existed
   *     10005 	access_token error
   *     10006 	access_token has expired, a new one should be acquired
   *
   *     Business Level Error Codes：
   *     Code 	Desc
   *     20001 	Account or Password Error
   *     20002 	Required Parameter missing (%s)
   *     20003 	Parameter Value Not Valid
   *     20004 	Account does not existed
   *     20005 	Not authorized to get the account info
   *     20006 	The count of targets under the account (%s) exceeds limitation
   *     20007 	IMEI (%s) is not existed
   *     20008 	Not authorized to get the target info
   *     20009 	Count of targets being requestd exceeds limitation
   *     20010 	Target (%s) has expired
   *     20011 	Map Type Error
   *     20012 	Latitude or Longitude Not Valid
   *     55555  Error Tripad (bisa berbagai macam sebab)
   */
  private void cekStatusResponse(String response) {
      //cek error message :
      
  }
  
  public TmcToken getToken() {
    final  OBCriteria<TmcToken> tmcTokenCrit = OBDal.getInstance().createCriteria(TmcToken.class);
    tmcTokenCrit.add(Restrictions.eq(TmcToken.PROPERTY_VALUE, "1"));
    
    if (tmcTokenCrit.count() > 0) {
        for (TmcToken tokenFromDB : tmcTokenCrit.list()) {
            return tokenFromDB;
        }
    }
    else {
        //dapatkan token dari server API lewat internet
        String getTokenUrl = COOTRACK_GET_TOKEN_URL +commonParameters(false);
        String jsonResponse = new CootrackHttpClient().post(getTokenUrl);
        JSONObject jsonData = null;
        try {
            jsonData = new JSONObject(jsonResponse);

            TmcToken tmcToken = OBProvider.getInstance().get(TmcToken.class);	
            tmcToken.setActive(true);
            tmcToken.setValue("1");        
            if ((int)jsonData.get("ret") == 0) {
                tmcToken.setToken(jsonData.get("access_token").toString());    
            }
            else {
                tmcToken.setToken("-");
            }
            tmcToken.setMessage(jsonData.get("msg").toString());
            tmcToken.setReturnCode(((int)jsonData.get("ret"))+"");  

            OBDal.getInstance().save(tmcToken);
            OBDal.getInstance().flush();

            OBDal.getInstance().commitAndClose();

            return tmcToken;
            
        } catch (JSONException ex) {
            Logger.getLogger(OpenApiUtils.class.getName()).log(Level.SEVERE, null, ex);
             throw new OBException("Error get Json ! : " + ex.getMessage());
        }
    }
    return null;
  }

  public long getUnixTime() {
    Date date = new Date();
    long unixTime = (long) date.getTime() / 1000;
    return unixTime;
  }

  public String convertToMd5(String param) {
    String password = param;
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
      md.update(password.getBytes());
    } catch (NoSuchAlgorithmException na) {
      throw new OBException("Error ! : " + na.getMessage());
    }

    byte byteData[] = md.digest();

    // convert the byte to hex format method 1
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }

    // System.out.println("Digest(in hex format):: " + sb.toString());

    // convert the byte to hex format method 2
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      String hex = Integer.toHexString(0xff & byteData[i]);
      if (hex.length() == 1)
        hexString.append('0');
      hexString.append(hex);
    }
    // System.out.println("Digest(in hex format):: " + hexString.toString());
    return hexString.toString();
  }

}
