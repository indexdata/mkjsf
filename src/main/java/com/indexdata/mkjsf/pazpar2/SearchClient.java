package com.indexdata.mkjsf.pazpar2;

import java.io.Serializable;

import com.indexdata.mkjsf.config.Configurable;
import com.indexdata.mkjsf.config.Configuration;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;

/**
 * Interface abstracting Pazpar2 and Service Proxy services. 
 * 
 * @author Niels Erik
 *
 */
public interface SearchClient extends Configurable, Serializable {
  
  /**
   * Search commands are saved for management purposes, like bootstrapping
   * expired sessions and write log statements. 
   * @param command
   */
  public void setSearchCommand(Pazpar2Command command);
  
  /**
   * Issues the provided command against the selected Pazpar2 service.
   * @param command
   * @return
   */
  public HttpResponseWrapper executeCommand(Pazpar2Command command);
  
  // Use cloneMe() method if injecting the client with CDI.
  // The client is used for asynchronously sending off requests
  // to the server AND propagation of context to threads is currently 
  // not supported. Trying to do so throws a WELD-001303 error. 
  // If propagation to threads gets supported, the cloning can go.
  public SearchClient cloneMe();
  
  /**
   * Basically says if this client accesses a Service Proxy, which (usually)
   * requires some form of authentication, or Pazpar2, which don't.
   * @return
   */
  public boolean isAuthenticatingClient();  
  
  /**
   * Returns the current client configuration - mainly for error resolution.
   * @return
   */
  public Configuration getConfiguration();
  
  /**
   * Returns the URL of the currently selected Pazpar2/SP service
   * @return URL as a String
   */
  public String getServiceUrl();
  
  /**
   * Sets the URL of the Pazpar2/SP service to use for searching. 
   * 
   * @param url
   */
  public void setServiceUrl(String url);
  
  /**
   * Returns true if a service has been selected, whether by configuration or runtime. 
   * @return true is service was selected, false otherwise.
   */
  public boolean hasServiceUrl();  
}
