package com.indexdata.pz2utils4jsf.config;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.masterkey.config.MissingMandatoryParameterException;
import com.indexdata.masterkey.config.ModuleConfiguration;
import com.indexdata.masterkey.config.ModuleConfigurationGetter;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named @SessionScoped
public class Pz2Config implements ModuleConfigurationGetter, Serializable {

  private static final long serialVersionUID = -6801241975338182197L;
  private static Logger logger = Logger.getLogger(Pz2Config.class);
  Map<String,String> properties = new HashMap<String,String>();
  ModuleConfiguration moduleConfig = null;
  
  public Pz2Config () {
    logger.debug(Utils.objectId(this) + " being constructed with no argument");
    setDefaults();
  }
  
  public Pz2Config (Map<String,String> parameters) {
    logger.debug(Utils.objectId(this) + " being constructed with parameter map argument");
    setDefaults();
    for (String key : parameters.keySet()) {
      properties.put(key, parameters.get(key));
    }
  }
  
  public Pz2Config (ModuleConfiguration moduleConfig) throws IOException {
    logger.debug(Utils.objectId(this) + " being constructed with moduleConfig argument.");
    this.moduleConfig = moduleConfig;
    for (String key : moduleConfig.getConfigMap().keySet()) {
      properties.put(key, moduleConfig.getConfigParameter(key));
    }
  }
  
  private void setDefaults () {
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
    return moduleConfig.getConfigFilePath();
  }
  
  

}
