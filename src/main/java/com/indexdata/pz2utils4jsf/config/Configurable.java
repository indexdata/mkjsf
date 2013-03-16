package com.indexdata.pz2utils4jsf.config;

import java.util.List;
import java.util.Map;

import com.indexdata.pz2utils4jsf.errors.ConfigurationException;

/**
 * Interface to be implemented by any part of an application that wish to
 * use a ConfigurationReader for it's configuration. The Configurables that
 * come with the project are a Pazpar2 client and a Service Proxy client
 * 
 * @author Niels Erik
 *
 */
public interface Configurable {

  /**
   * Configures the Configurable using the configuration obtained by the 
   * provided configuration reader
   * @param reader used for reading the configuration 
   * @throws ConfigurationException
   */
  public void configure(ConfigurationReader reader) throws ConfigurationException;
  
  /**
   * Returns the default parameters that the configurable has defined for itself
   * Should be invoked by the configuration reader before it possibly overrides
   * some parameters obtained from the external configuration source  
   * @return
   */
  public Map<String,String> getDefaults();
  
  /**
   * Returns the name of the module, can be used by a configuration reader that 
   * has distinguishes between sets of configuration properties by component name
   * @return name of the part that is to be configured
   */
  public String getModuleName();
  
  /**
   * The components documentation of how it was configured. 
   * 
   * @return a list of Strings describing configuration details
   */
  public List<String> documentConfiguration(); 
  
}
