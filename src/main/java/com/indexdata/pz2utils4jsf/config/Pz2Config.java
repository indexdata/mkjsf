package com.indexdata.pz2utils4jsf.config;

import java.util.HashMap;
import java.util.Map;

import com.indexdata.masterkey.config.MissingMandatoryParameterException;
import com.indexdata.masterkey.config.ModuleConfigurationGetter;

public class Pz2Config implements ModuleConfigurationGetter {

  Map<String,String> properties = new HashMap<String,String>();
  
  public Pz2Config () {
    setStatics();
  }
  
  public Pz2Config (String pazpar2Url, String pazpar2ServiceId) {
    setStatics();
    setPazpar2Url(pazpar2Url);
    setPazpar2ServiceId(pazpar2ServiceId);
  }
  
  private void setStatics () {
    properties.put("PROXY_MODE","1");
    properties.put("SERIALIZE_REQUESTS", "false");
    properties.put("STREAMBUFF_SIZE", "4096");
    properties.put("PARSE_RESPONSES", "true");    
  }
  
  @Override
  public String get(String key) {
    return properties.get(key);    
  }
  
  public void set(String key, String value) {
    properties.put(key, value);
  }
  
  public void setPazpar2Url (String value) {
    properties.put("PAZPAR2_URL", value);
  }
  
  public void setPazpar2ServiceId (String value) {
    properties.put("PAZPAR2_SERVICE_ID",value);
  }

  @Override
  public String get(String key, String defaultValue) {
    if (properties.containsKey(key)) {
      return properties.get(key);
    } else {
      return defaultValue;
    }
  }

  @Override
  public String getMandatory(String key) throws MissingMandatoryParameterException {
    if (properties.containsKey(key)) {
      return properties.get(key);
    } 
    throw new Error("Missing mandatory parameter: " + key);     
  }

  @Override
  public String getConfigFilePath() {
    return null;
  }
  
  

}
