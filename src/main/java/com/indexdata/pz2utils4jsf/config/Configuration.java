package com.indexdata.pz2utils4jsf.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.indexdata.masterkey.config.MissingMandatoryParameterException;
import com.indexdata.pz2utils4jsf.utils.Utils;

public class Configuration implements Serializable {

  private static final long serialVersionUID = -6801241975338182197L;
  private static Logger logger = Logger.getLogger(Configuration.class);
  Map<String,String> properties = new HashMap<String,String>();
  
  public Configuration () {
    logger.debug(Utils.objectId(this) + " being constructed with no argument");    
  }
  
  public Configuration(Map<String,String> parameters) {
    addAll(parameters);
  }
  
  public void addAll(Map<String,String> parameters) {
    for (String key : parameters.keySet()) {
      properties.put(key, parameters.get(key));
    }    
  }
  
  public void addAll(Map<String,String> defaults, Map<String,String> parameters) {
    for (String key : defaults.keySet()) {
      properties.put(key, defaults.get(key));
    }    
    for (String key : parameters.keySet()) {
      properties.put(key, parameters.get(key));
    }    
  }
          
  public String get(String key) {
    return properties.get(key);    
  }
  
  public void set(String key, String value) {
    properties.put(key, value);
  }
    
  public String get(String key, String defaultValue) {
    if (properties.containsKey(key)) {
      return properties.get(key);
    } else {
      return defaultValue;
    }
  }
  
  public String getMandatory(String key) throws MissingMandatoryParameterException {
    if (properties.containsKey(key)) {
      return properties.get(key);
    } 
    throw new MissingMandatoryParameterException("Missing mandatory parameter: " + key);     
  }

  public String getConfigFilePath() {
    return get("configfilepath","nopathgiven");
  }
  
  public Map<String,String> getConfigMap() {
    return properties;
  }
  

}
