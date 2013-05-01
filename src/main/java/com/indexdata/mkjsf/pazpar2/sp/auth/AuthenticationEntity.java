package com.indexdata.mkjsf.pazpar2.sp.auth;

import java.io.Serializable;
import java.util.Map;

public interface AuthenticationEntity extends Serializable{
  
  
  public String getProperty(String key); 
  
  public Map<String,String> getPropertyMap();
  
}
