package com.indexdata.mkjsf.pazpar2.data.sp;

public class AuthResponse extends SpResponseDataObject  {

  private static final long serialVersionUID = 8006774126022849936L;

  public String getDisplayName () {
    return getOneElementValue("displayName");
  }
  
  public String getRealm () {
    return getOneElementValue("realm");
  }
  
  public String getStatus () {
    return getOneElementValue("status");
  }
  
  public String getAuthenticationType () {
    return getOneElementValue("type");
  }
  
  public String onSuccess(String navigateTo) {
    return navigateTo;
  }
  
  
}
