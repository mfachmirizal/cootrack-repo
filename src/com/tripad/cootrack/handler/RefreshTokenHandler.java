package com.tripad.cootrack.handler;

import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.client.kernel.BaseActionHandler;

import com.tripad.cootrack.utility.OpenApiUtils;

public class RefreshTokenHandler extends BaseActionHandler {

  @SuppressWarnings("finally")
  protected JSONObject execute(Map<String, Object> parameters, String data) {
    String hasil = "";
    JSONObject json = new JSONObject();
    OpenApiUtils utils = new OpenApiUtils();
    try {
      if (utils.getCurrentPassword() != null) {
        hasil = utils.getToken().getMessage();
        //hasil = utils.getListMonitoring();

        // String isProcessed = "N";

        // get the data as json
        // final JSONObject jsonData = new JSONObject(data);
        // final JSONArray headerIds = jsonData.getJSONArray("headers");
        //
        // // iterate over the headersid
        // for (int i = 0; i < headerIds.length(); i++) {
        // final String headerId = headerIds.getString(i);
        //
        // // get the order
        // final GudUjianDinas gudUjianDinas = OBDal.getInstance().get(GudUjianDinas.class, headerId);
        //
        // // and add its grand totalheaderId
        // isProcessed = gudUjianDinas.isProcessed().toString();
        // }

        // create the result
        json.put("jawaban", hasil);
      } else {
        json.put("jawaban", "This Openbravo user not synchronized with OpenAPI !");
      }
    } catch (JSONException e) {
      json.put("jawaban", e.getMessage());
    } finally {
      return json;
    }
  }
}