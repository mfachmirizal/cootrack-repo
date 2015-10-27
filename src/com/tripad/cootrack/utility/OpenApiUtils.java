package com.tripad.cootrack.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.openbravo.base.exception.OBException;
// import org.openbravo.base.exception.OBException;
// import org.openbravo.client.kernel.BaseActionHandler;
// import org.openbravo.dal.service.OBDal;
// import org.openbravo.model.common.order.Order;

public class OpenApiUtils {
  // Nanti static var ini hapus
  public static final String COOTRACK_USERNAME = "enduserdahlia";
  public static final String COOTRACK_PASSWORD = "123456";
  // static var
  public static final String COOTRACK_GET_TOKEN_URL = "http://api.gpsoo.net/1/auth/access_token?";

  public OpenApiUtils() {
  }

  public String getToken() {
    String getTokenUrl = COOTRACK_GET_TOKEN_URL + "account=" + COOTRACK_USERNAME + "&time="
        + getUnixTime() + "&signature="
        + (convertToMd5((convertToMd5(COOTRACK_PASSWORD)) + getUnixTime()));
    // post ke server dan dapatkan json response
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
