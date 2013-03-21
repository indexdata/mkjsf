package com.indexdata.pz2utils4jsf.pazpar2.sp.auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("user") @SessionScoped
public class ServiceProxyUser implements AuthenticationEntity {

  private static final long serialVersionUID = 2351542518778803071L;
  private List<String> possibleProperties = Arrays.asList("name","password","realm");
  private Map<String,String> actualProperties = new HashMap<String,String>();

  public ServiceProxyUser()  {}
  
  public void setAuthenticationMethod() {
    
  }

  public String getName() { 
    return actualProperties.get("name"); 
  }
  
  public void setName(String newValue) { 
    actualProperties.put("name", newValue); 
  }
  
  public String getPassword() { 
    return actualProperties.get("password"); 
  }
  
  public void setPassword(String newValue) { 
    actualProperties.put("password", newValue);
  }
  
  public void setRealm(String realm) {
    actualProperties.put("realm", realm);
  }
  
  public String getRealm() {
    return actualProperties.get("realm");
  }
  

  @Override
  public String getProperty(String key) {
    return actualProperties.get(key);
  }

  @Override
  public Map<String, String> getPropertyMap() {
    return actualProperties;
  }

  @Override
  public List<String> getPossibleProperties() {
    return possibleProperties;
  } 
  

}
