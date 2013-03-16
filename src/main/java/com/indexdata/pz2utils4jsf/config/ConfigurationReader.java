package com.indexdata.pz2utils4jsf.config;

import java.io.Serializable;
import java.util.List;

import com.indexdata.pz2utils4jsf.errors.ConfigurationException;

/**
 * Interface to be implemented by classes that read configurations from a source -
 * i.e. from web.xml, the file system, a database or hard-coded. 
 * 
 * @author Niels Erik
 *
 */
public interface ConfigurationReader extends Serializable {
    
  /**
   * Returns a Configuration to be used by the given Configurable
   * 
   * @param configurable the configurable to be configured by a configuration obtained by this reader
   * @return a Configuration, basically a set of key-value pairs
   * @throws ConfigurationException
   */
  public Configuration getConfiguration(Configurable configurable) throws ConfigurationException;
  
  /**
   * Returns documentation for the key-value pairs obtained by this reader
   * @return
   */
  public List<String> document();
}
