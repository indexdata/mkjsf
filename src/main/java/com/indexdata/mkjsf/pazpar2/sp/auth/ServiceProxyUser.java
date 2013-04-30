package com.indexdata.mkjsf.pazpar2.sp.auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.utils.Utils;

@Named("user") @SessionScoped
public class ServiceProxyUser implements AuthenticationEntity {

  private static final long serialVersionUID = 2351542518778803071L;
  private List<String> possibleProperties = Arrays.asList("name","password","realm");
  private Map<String,String> actualProperties = new HashMap<String,String>();
  private static Logger logger = Logger.getLogger(ServiceProxyUser.class);
  private boolean authenticated = false;
  private boolean ipAuthenticated = false;

  public ServiceProxyUser()  {
    logger.debug("ServiceProxyUser instantiated: " + Utils.objectId(this));
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
   
  public void isAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;
  }
  
  public void isIpAuthenticated (boolean authenticated) {
    this.ipAuthenticated = authenticated;
  }
  
  public boolean isAuthenticated() {
    return authenticated;
  }
  
  public boolean isIpAuthenticated () {
    return ipAuthenticated;
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
  
  public void clear() {
    actualProperties = new HashMap<String,String>();
    authenticated = false;
    ipAuthenticated = false;
  }
  

}
