package com.indexdata.mkjsf.pazpar2.sp.auth;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.utils.Utils;

@Named("user") @SessionScoped
public class ServiceProxyUser implements AuthenticationEntity {

  private static final long serialVersionUID = 2351542518778803071L;
  private Map<String,String> actualProperties = new HashMap<String,String>();
  private static Logger logger = Logger.getLogger(ServiceProxyUser.class);
  private boolean credsAuthenticated = false;
  private boolean ipAuthenticated = false;
  private boolean ipAuthFailure = false;
  private boolean credsAuthFailure = false;  

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
   
  public void credentialsAuthenticationSucceeded (boolean success) {
    this.credsAuthFailure = !success;
    this.credsAuthenticated = success;
    this.ipAuthenticated = false;
    this.ipAuthFailure = false;
  }
  
  public void ipAuthenticationSucceeded (boolean success) {
    this.ipAuthFailure = !success;    
    this.ipAuthenticated = success;
    this.credsAuthenticated = false;
    this.credsAuthFailure = false;
  }
  
  public boolean isAuthenticated() {
    return (ipAuthenticated || credsAuthenticated);
  }
  
  public boolean isIpAuthenticated () {
    return ipAuthenticated;
  }
  
  public boolean isCredentialsAuthenticated () {
    return credsAuthenticated;
  }
  
  public boolean hasIpAuthFailure () {
    return ipAuthFailure;
  }
  
  public boolean hasCredsAuthFailure () {
    return credsAuthFailure;
  }
  
  public boolean hasAuthenticationFailure () {
    return credsAuthFailure || ipAuthFailure;
  }
  
  public void authenticationCheckFailed () {
    ipAuthenticated = false;
    credsAuthenticated = false;
  }
  
  public String getAuthenticationStatus () {
    return (isAuthenticated() ? 
              (isIpAuthenticated()? "IP authenticated" : 
                 (isCredentialsAuthenticated() ? "Authenticated by credentials" : "Unknown authentication method")) :
              (hasAuthenticationFailure() ? 
                  (hasIpAuthFailure() ? "Authentication by IP address failed" :
                      (hasCredsAuthFailure() ? "Authentication by credentials failed" : "Unknown authentication failure")) :
                "Not authenticated"));
  }
  

  @Override
  public String getProperty(String key) {
    return actualProperties.get(key);
  }

  @Override
  public Map<String, String> getPropertyMap() {
    return actualProperties;
  }
  
  public void clear() {
    actualProperties = new HashMap<String,String>();
    credsAuthenticated = false;
    ipAuthenticated = false;    
  }
  
  public void setSpResponse (String responseXml) {
    
  }
  

}
