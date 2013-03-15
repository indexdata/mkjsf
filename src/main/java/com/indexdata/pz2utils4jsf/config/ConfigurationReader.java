package com.indexdata.pz2utils4jsf.config;

import java.io.Serializable;
import java.util.List;

import com.indexdata.pz2utils4jsf.errors.ConfigurationException;

public interface ConfigurationReader extends Serializable {
    
  public Configuration getConfiguration(Configurable configurable) throws ConfigurationException;
  
  public List<String> document();
}
