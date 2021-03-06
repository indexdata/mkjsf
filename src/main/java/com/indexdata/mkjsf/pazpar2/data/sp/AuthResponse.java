package com.indexdata.mkjsf.pazpar2.data.sp;

/**
 * Data from the <code>auth</code> command, can be accessed by <code>pzresp.sp.auth</code>
 * 
 * @author Niels Erik
 *
 */
public class AuthResponse extends SpResponseDataObject  {

  private static final long serialVersionUID = 8006774126022849936L;

  public String getDisplayName () {
    return getOneValue("displayName");
  }
  
  public String getRealm () {
    return getOneValue("realm");
  }
  
  public String getStatus () {
    return getOneValue("status");
  }
  
  public String getAuthenticationType () {
    return getOneValue("type");
  }
  
  public String onSuccess(String navigateTo) {
    if (isAuthenticationOk()) {
      return navigateTo;
    } else {
      return null;
    }
  }
  
  public boolean isAuthenticationOk () {
    return getStatus().equalsIgnoreCase("OK");
  }    
}
