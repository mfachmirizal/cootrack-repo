package com.tripad.cootrack.modulescript;

import java.sql.PreparedStatement;

import org.openbravo.database.ConnectionProvider;
import org.openbravo.modulescript.ModuleScript;

/****
 * Oleh : Moch Fachmi Rizal @ Tripad
 */
 
public class MSFilterBusinessPartnerPerUser extends ModuleScript {
 
  public void execute() {
    try {
      ConnectionProvider cp = getConnectionProvider();
      PreparedStatement ps = cp
          .getPreparedStatement("UPDATE AD_TAB SET hqlwhereclause='e.createdBy.id=@AD_USER_ID@' WHERE AD_TABLE_ID = '291' AND AD_WINDOW_ID='123' AND tablevel=0");
      ps.executeUpdate();
    } catch (Exception e) {
      handleError(e);
    }
  }
}

//ant compile.modulescript -Dmodule=com.tripad.cootrack.modulescript