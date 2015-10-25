package com.tripad.cootrack.utility;

import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import java.util.Date;
import java.lang.Long;
// import org.openbravo.base.exception.OBException;
// import org.openbravo.client.kernel.BaseActionHandler;
// import org.openbravo.dal.service.OBDal;
// import org.openbravo.model.common.order.Order;

public class OpenApiUtils  {
    //Nanti static var ini hapus
	public static final String COOTRACT_USERNAME =  "enduserdahlia";
	public static final String COOTRACT_PASSWORD =  "123456";
	
    public OpenApiUtils() {
    }
	
	public String getToken() {
		//http://api.gpsoo.net/1/auth/access_token?account=demobkt&time=1445586322&signature=c4fc3f5175a41e63bc5b33eda62fa1b5
		return null;
	}
	
	public long getUnixTime() {
		Date date = new Date();
		long unixTime = (long) date.getTime()/1000;
		return unixTime;
	}
	
	
}
