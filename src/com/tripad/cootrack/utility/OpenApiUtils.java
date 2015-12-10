package com.tripad.cootrack.utility;

/****
 * Oleh : Moch Fachmi Rizal @ Tripad
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.joda.time.Interval;
import org.openbravo.base.exception.OBException;
import org.openbravo.base.provider.OBProvider;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBCriteria;
//import org.openbravo.client.kernel.BaseActionHandler;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.ad.access.User;

import com.tripad.cootrack.data.TmcToken;
import com.tripad.cootrack.data.TmcUserSync;

public class OpenApiUtils {
  // Nanti static var ini hapus
  public User COOTRACK_USERNAME = OBContext.getOBContext().getUser();// "enduserdahlia";

  public String COOTRACK_PASSWORD = getCurrentPassword();
  // static var
  public static final String COOTRACK_GET_TOKEN_URL = "http://api.gpsoo.net/1/auth/access_token?";
  public static final String COOTRACK_GET_TARGET_INFO = "http://api.gpsoo.net/1/account/devinfo?target";
  public static final String COOTRACK_GET_MONITOR_URL_BY_TARGET = "http://api.gpsoo.net/1/account/monitor?target";
  public static final String COOTRACK_GET_TRACKING_URL_BY_IMEI = "http://api.gpsoo.net/1/devices/tracking?map_type=GOOGLE&imeis";

  public OpenApiUtils() {

  }

 /*
  private String commonParameters(boolean tokenIsAlreadyExist) throws NullPointerException {
    if (tokenIsAlreadyExist) {
      TmcToken token = getToken();
      if (token.getReturnCode().equals("20001") ||token.getReturnCode().equals("20004") ) {
        return "Wrong Username / Password !";
      } else if (token.getReturnCode().equals("0")) {
        return "access_token=" + token.getToken() + "&account=" + COOTRACK_USERNAME.getUsername()
            + "&time=" + getUnixTime();
      }
      else {
        return "Unknown error when retrieve !";
      }
    } else {
      return "account=" + COOTRACK_USERNAME.getUsername() + "&time=" + getUnixTime() + "&signature="
          + (convertToMd5((COOTRACK_PASSWORD) + getUnixTime()));
    }
  }
  */
  private JSONObject requestData(String action,String... param) throws JSONException,Throwable {
      String commonParam = "";
      TmcToken token = getToken();
      if (token == null) {
        return new CustomJsonErrorResponse(token.getReturnCode(), "Failed to get Token !").getJSONErrResponse();
      }
      if (token.getReturnCode().equals("0")) {
        commonParam = "access_token=" + token.getToken() + "&account=" + COOTRACK_USERNAME.getUsername()
        + "&time=" + getUnixTime();
      } else if (token.getReturnCode().equals("20001") ||token.getReturnCode().equals("20004") ) {
        return new CustomJsonErrorResponse(token.getReturnCode(), "Wrong Username / Password !").getJSONErrResponse();
      } else {
        return new CustomJsonErrorResponse(token.getReturnCode(), token.getMessage()+", Delete Token and try again").getJSONErrResponse();
      }
      
    String url = "";
    if (action.equals("tracking")) {
      url = COOTRACK_GET_TRACKING_URL_BY_IMEI + "=" + param[0] + "&" + commonParam;
    } else if (action.equals("info")) {
      if (param[0] == null) {
        param[0] = encodeString(COOTRACK_USERNAME.getUsername());
      }
      if ("".equals(param[0])) {
        param[0] = encodeString(COOTRACK_USERNAME.getUsername());
      }
      url = COOTRACK_GET_TARGET_INFO + "=" + param[0] + "&" + commonParam;
    }

    String jsonResponse = "";
    jsonResponse = new CootrackHttpClient().post2(url);
    
    JSONObject hasil = new JSONObject(jsonResponse);
    
    //System.out.println("Hasil return : "+hasil.get("ret").toString());
    //auto get token yg expired belum di tambahkan
    if ((hasil.get("ret").toString()).equals("10005") || (hasil.get("ret").toString()).equals("10006") )   { //token error / not existed 
        System.out.println("Token Habis : "+hasil.get("ret").toString());
        deleteToken();
        hasil = requestData(action,param);
    }
    //System.out.println("Lewat Pengecekan Token : "+hasil.get("ret").toString());
    
    if ((hasil.get("ret").toString()).equals("10101")) { // 10101 : ip limit
      JSONObject hasilRetry;
      do {
        try {
          Thread.sleep(85);
        } catch (InterruptedException e) {
          return new CustomJsonErrorResponse("5151", "Thread Error : "+e.getMessage()).getJSONErrResponse();
        }
        hasilRetry = retryRequest(url);
      } while (hasilRetry.get("ret").toString().equals("10101"));
      //return hasilRetry;
    }
    
    if (hasil.get("ret").toString().equals("5555")) {
      System.out.println("Masuk 5555");
      throw new Throwable(hasil.get("msg").toString());
    }

    return hasil;
  }

  /*
   * private String commonParameters(boolean tokenIsAlreadyExist) { System.out.println("USER  : "
   * +COOTRACK_USERNAME); if (tokenIsAlreadyExist) { // if (getToken() != null) { TmcToken token =
   * getToken(); if (token != null) { return "access_token=" + token.getToken() + "&account=" +
   * COOTRACK_USERNAME + "&time=" + getUnixTime(); } else { return noTokenParam(); }
   *
   * // } else { // return "account=" + COOTRACK_USERNAME + "&time=" + getUnixTime() + "&signature="
   * // + (convertToMd5((convertToMd5(COOTRACK_PASSWORD)) + getUnixTime())); // } }
   *
   * private String noTokenParam() { return "account=" + COOTRACK_USERNAME + "&time=" +
   * getUnixTime() + "&signature=" + (convertToMd5((convertToMd5(COOTRACK_PASSWORD)) +
   * getUnixTime())); }
   */
  /**
   * method untuk memeriksa hasil response dari api server
   *
   * @param response
   *          berupa message dari server : Code Desc 10001 System Error 10002 The API you request
   *          does not exist 10003 Request frequence exceeds limitation 10004 access_token not
   *          existed 10005 access_token error 10006 access_token has expired, a new one should be
   *          acquired
   *
   *          Business Level Error Codes： Code Desc 20001 Account or Password Error 20002 Required
   *          Parameter missing (%s) 20003 Parameter Value Not Valid 20004 Account does not existed
   *          20005 Not authorized to get the account info 20006 The count of targets under the
   *          account (%s) exceeds limitation 20007 IMEI (%s) is not existed 20008 Not authorized to
   *          get the target info 20009 Count of targets being requestd exceeds limitation 20010
   *          Target (%s) has expired 20011 Map Type Error 20012 Latitude or Longitude Not Valid
   *          55555 Error Tripad (bisa berbagai macam sebab)
   */
  private JSONObject checkAndProcessStatusResponse(JSONObject response) throws JSONException {
    // cek error message :
    int result = 55555;
    try {
      result = Integer.parseInt(response.get("ret").toString());

      System.out.println("Hasil Result : " + result);
      /*
       * 10001 System Error 10002 The API you request does not exist 10003 Request frequence exceeds
       * limitation 10004 access_token not existed 10005 access_token error 10006 access_token has
       * expired, a new one should be acquired
       *
       * Business Level Error Codes： 20001 Account or Password Error 20002 Required Parameter
       * missing (%s) 20003 Parameter Value Not Valid 20004 Account does not existed 20005 Not
       * authorized to get the account info 20006 The count of targets under the account (%s)
       * exceeds limitation 20007 IMEI (%s) is not existed 20008 Not authorized to get the target
       * info 20009 Count of targets being requestd exceeds limitation 20010 Target (%s) has expired
       * 20011 Map Type Error 20012 Latitude or Longitude Not Valid
       */
      if (result == 0) {
        return response; // sukses
        // dibawah ini line code untuk debug
        // JSONArray headerIds = (JSONArray) response.get("data");
        // String rslt = "";
        // for (int i = 0; i < headerIds.length(); i++) {
        // rslt += headerIds.getString(i);
        // }
        // return rslt;
      } /*else if (result == 10006) {
        // token expired, ambil yg baru
        deleteToken();
        // System.out.println("Memasuki delete token"); // getToken();
        if (getToken() != null) {
          // return new CustomJsonErrorResponse("5555",
          // getToken().getMessage()).getJSONErrResponse(); //getToken().getMessage();
          // lakukan request ulang
          // System.out.println("Retry request dikarenakan token expired");
          // System.out.println("Memasuki Retry Request");
          return retryRequest(url);

        } else { // akan deprecated
          return new CustomJsonErrorResponse("5555",
              "Gagal Mendapatkan Token Kembali !,Username Tidak terdapat di openAPI")
                  .getJSONErrResponse();
        }
      } */
      else { // error code lain

        if ((response.get("ret").toString()).equals("20004")
            || (response.get("ret").toString()).equals("20001")) { // account tidak ada

          System.out.println("user tidak da di open api : "
              + new CustomJsonErrorResponse("5151", "User ini tidak terdapat di OpenAPi")
                  .getJSONErrResponse().toString());

          return new CustomJsonErrorResponse("5151", "User ini tidak terdapat di OpenAPI")
              .getJSONErrResponse();
        }/* else if ((response.get("ret").toString()).equals("10101")) { // 10101 : ip limit
          JSONObject hasilRetry;
          do {
            Thread.sleep(91);
            hasilRetry = retryRequest(url);
          } while (hasilRetry.get("ret").toString().equals("10101"));
          return hasilRetry;
        } else {
          return new CustomJsonErrorResponse("5555",
              "Error Lain - " + (response.get("msg").toString())).getJSONErrResponse();
        } */
      }
    } catch (NumberFormatException e) {
        return new CustomJsonErrorResponse("5555", "Return Message bukan angka !").getJSONErrResponse();
    } catch (JSONException e) {
      return new CustomJsonErrorResponse("5555","OpenApiUtils, JSON Exception !" + e.getMessage()).getJSONErrResponse();
    } catch (Throwable z) {
      //throw new OBException("Error [OpenApiUtils] ! : " + z.getMessage());// return new
       return new CustomJsonErrorResponse("5555","OpenApiUtils, JSON Exception !" +z.getMessage()).getJSONErrResponse();
    }
    return new CustomJsonErrorResponse("5151", "Unknown Error when checking Status Response")
        .getJSONErrResponse();
  }

  private JSONObject retryRequest(String url)  {
    String jsonResponse = "";
    jsonResponse = new CootrackHttpClient().post2(url);
    JSONObject jsonData =null;
    try {
      System.out.println("Melakukan retry request");
      jsonData = new JSONObject(jsonResponse);
      System.out.println("Hasill json retry : " + jsonData.toString());
      
    } catch (JSONException e) {
      //throw new OBException("Error [OpenApiUtils] ! : " + e.getMessage());// return new
      jsonData = new CustomJsonErrorResponse("5555",
       "Error : " +
       e.getMessage()
       ).getJSONErrResponse();
    }
    finally {
      return jsonData;
    }
  }

  // @deprecated
  private String encode(String param) {

    return param;
  }

  /**
   *
   * @param target
   *          Target user yg akan di dapatkan informasinya, bila kosong target = Dealer
   * @return Hasil Response berupa List Informasi User dari OpenAPI
   */
  
  /* @Deprecated
  public JSONObject requestListChildAccount(String target) throws JSONException {
    if (target == null) {
      target = encodeString(COOTRACK_USERNAME.getUsername());
    }
    if ("".equals(target)) {
      target = encodeString(COOTRACK_USERNAME.getUsername());
    }

    String getListChildAccountUrl = "";
    getListChildAccountUrl = COOTRACK_GET_TARGET_INFO + "=" + target + "&" + commonParameters(true);
    String jsonResponse = "";
    jsonResponse = new CootrackHttpClient().post2(getListChildAccountUrl);
    JSONObject jsonData;
    jsonData = new JSONObject(jsonResponse);
    // cek status response dari server

    return checkAndProcessStatusResponse(jsonData, getListChildAccountUrl);
  }
*/
  public JSONObject requestListChildAccount(String target) throws JSONException,Throwable{
    JSONObject jsonData;
    jsonData = requestData("info",target);
    
    // cek status response dari server

   // return checkAndProcessStatusResponse(jsonData);
    return jsonData;
  }
  
  /*
   * @Deprecated public JSONObject requestListMonitoring() { String getMonitorUrl =
   * COOTRACK_GET_MONITOR_URL_BY_TARGET + "=" + encodeString("Bangun") + "&" +
   * commonParameters(true);
   * 
   * String jsonResponse = ""; jsonResponse = new CootrackHttpClient().post2(getMonitorUrl);
   * JSONObject jsonData; try { jsonData = new JSONObject(jsonResponse); } catch (JSONException e) {
   * throw new OBException("Error [OpenApiUtils] ! : " + e.getMessage());// return new } // cek
   * status response dari server
   * 
   * return checkAndProcessStatusResponse(jsonData, getMonitorUrl); }
   */
  
  /* @Deprecated
  public JSONObject requestStatusFilteredCarByImei(String imeis) throws JSONException {
    String commonParam = commonParameters(true);
    if (commonParam.equals("TokenFailed")) {
      return new CustomJsonErrorResponse("5151", "Gagal Mendapatkan Token [User / Password salah] ")
          .getJSONErrResponse();
    }
    String getTrackingUrl = COOTRACK_GET_TRACKING_URL_BY_IMEI + "=" + imeis + "&" + commonParam;

    String jsonResponse = "";
    jsonResponse = new CootrackHttpClient().post2(getTrackingUrl);
    JSONObject jsonData;
    // try {
    jsonData = new JSONObject(jsonResponse);
    System.out.println("lewat sini");
    return checkAndProcessStatusResponse(jsonData, getTrackingUrl);
  }

*/
  public JSONObject requestStatusFilteredCarByImei(String imeis) throws JSONException,Throwable {
    JSONObject jsonData;
    jsonData = requestData("tracking",imeis);
    
    //return checkAndProcessStatusResponse(jsonData);
    return jsonData;
  }

  public void deleteToken() {
    final OBCriteria<TmcToken> tmcTokenCrit = OBDal.getInstance().createCriteria(TmcToken.class);
    tmcTokenCrit.add(Restrictions.eq(TmcToken.PROPERTY_VALUE, "1"));
    tmcTokenCrit.add(Restrictions.eq(TmcToken.PROPERTY_CREATEDBY, COOTRACK_USERNAME));
    for (TmcToken tokenFromDB : tmcTokenCrit.list()) {
      OBDal.getInstance().remove(tokenFromDB);
      OBDal.getInstance().flush();
      OBDal.getInstance().commitAndClose();
    }
  }

  public TmcToken getToken() {
    final OBCriteria<TmcToken> tmcTokenCrit = OBDal.getInstance().createCriteria(TmcToken.class);
    tmcTokenCrit.add(Restrictions.eq(TmcToken.PROPERTY_VALUE, "1"));
    tmcTokenCrit.add(Restrictions.eq(TmcToken.PROPERTY_CREATEDBY, COOTRACK_USERNAME));

    if (tmcTokenCrit.count() > 0) {
      for (TmcToken tokenFromDB : tmcTokenCrit.list()) {
        if (tokenFromDB.getMessage().equals("10006")) {// token expired, delete dan dapatkan kembali
          deleteToken();
          getToken();
        } //else if 5555
        else {
          return tokenFromDB;
        }
      }
      
    } else {
      // dapatkan token dari server API lewat internet
      TmcToken tmcToken = OBProvider.getInstance().get(TmcToken.class);
      String tokenParam = "account=" + COOTRACK_USERNAME.getUsername() + "&time=" + getUnixTime() + "&signature="
              + (convertToMd5(COOTRACK_PASSWORD + getUnixTime()));
      String getTokenUrl = COOTRACK_GET_TOKEN_URL + tokenParam;
      String jsonResponse = new CootrackHttpClient().post2(getTokenUrl);
      JSONObject jsonData = null;

      try {
        jsonData = new JSONObject(jsonResponse);
        //jsonData = checkAndProcessStatusResponse(jsonData);
        
        tmcToken.setActive(true);
        tmcToken.setValue("1");
        if (Integer.parseInt(jsonData.get("ret").toString()) == 0) {
            tmcToken.setToken(jsonData.get("access_token").toString());
         } else {
         tmcToken.setToken("-");
         }
        tmcToken.setMessage(jsonData.get("msg").toString());
        tmcToken.setReturnCode((Integer.parseInt(jsonData.get("ret").toString())) + "");

        OBDal.getInstance().save(tmcToken);
        OBDal.getInstance().flush();

        OBDal.getInstance().commitAndClose();

        return tmcToken;

      } catch (JSONException ex) {
         tmcToken.setActive(true);
         tmcToken.setValue("1");
         tmcToken.setToken("-");
         tmcToken.setMessage(ex.getMessage());
         tmcToken.setReturnCode("55555");
        // Logger.getLogger(OpenApiUtils.class.getName()).log(Level.SEVERE, null, ex);
        return tmcToken;
      }
    }
    return null;
  }

  public long getUnixTime() {
    Date date = new Date();
    long unixTime = (long) date.getTime() / 1000;
    return unixTime;
  }

  private String encodeString(String param) {
    try {
      return URLEncoder.encode(param, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new OBException("Error ! : " + e.getMessage());
    }
  }

  /**
   *
   * @param param1
   *          waktu awal dalam format unix
   * @param param2
   *          waktu akhir dalam format unix
   * @param get
   *          tipe periode yg ingin di dapatkan (years,months,weeks,days,hours,minutes,seconds)
   * @return interval dalam int, dari 2 parameter yg di inputkan
   */
  public int getIntervalFromUnix(long param1, long param2, String get) {
      int hasil = 0;
      try {
          // Interval interval = new Interval( ((long)param1*1000), ((long)param2*1000) );
          Interval interval = new Interval((param1 * 1000L), (param2 * 1000L));
          get = get.toLowerCase();
          
          // if ("years".equals(get)) {
          // hasil = interval.toPeriod().getYears();
          // }else if ("months".equals(get)) {
          // hasil = interval.toPeriod().getMonths();
          // }else if ("weeks".equals(get)) {
          // hasil = interval.toPeriod().getWeeks();
          // }
          if ("days".equals(get)) {
              // hasil = interval.toPeriod().getDays();
              hasil = interval.toDuration().toStandardDays().getDays();
          } else if ("hours".equals(get)) {
              hasil = interval.toDuration().toStandardHours().getHours();
          } else if ("minutes".equals(get)) {
              hasil = interval.toDuration().toStandardMinutes().getMinutes();
          } else if ("seconds".equals(get)) {
              hasil = interval.toDuration().toStandardSeconds().getSeconds();
              
          } else {
              throw new OBException(get + " bukan parameter yg tepat untuk method : +getIntervalFromUnix");
          }
      } catch(Exception e) {
          hasil = 0;
      }
      return hasil;
  }

  // public String debugTanggal(long param1) {
  // DateTime dt = new DateTime(param1 * 1000L)
  // return null;
  // }

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

  public String[] convertToArray(String src, String delim) {
    String hasil[] = src.split(delim);
    return hasil;
  }

  public String getCurrentPassword() {
    OBCriteria<TmcUserSync> tmcUserSyncCrit = OBDal.getInstance().createCriteria(TmcUserSync.class);
    tmcUserSyncCrit.add(Restrictions.eq(TmcUserSync.PROPERTY_USER, COOTRACK_USERNAME));

    if (tmcUserSyncCrit.count() > 0) {
      for (TmcUserSync tmcUserSync : tmcUserSyncCrit.list()) {
        return tmcUserSync.getPassword();
      }
    }
    return null;
  }

}
