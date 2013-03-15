package com.indexdata.pz2utils4jsf.config;

import java.util.List;
import java.util.Map;

import com.indexdata.pz2utils4jsf.errors.ConfigurationException;

public interface Configurable {

  public void configure(ConfigurationReader reader) throws ConfigurationException;
  public Map<String,String> getDefaults();
  public String getModuleName();
  public List<String> documentConfiguration(); 
  
}
