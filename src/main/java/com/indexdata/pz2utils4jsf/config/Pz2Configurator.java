package com.indexdata.pz2utils4jsf.config;

import java.io.Serializable;
import java.util.List;

import com.indexdata.pz2utils4jsf.errors.ConfigurationException;

public interface Pz2Configurator extends Serializable {
  public Pz2Config getConfig() throws ConfigurationException;
  
  public List<String> document();
}
