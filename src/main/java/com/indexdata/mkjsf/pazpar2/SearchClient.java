package com.indexdata.mkjsf.pazpar2;

import java.io.Serializable;

import com.indexdata.mkjsf.config.Configurable;
import com.indexdata.mkjsf.config.Configuration;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;

public interface SearchClient extends Configurable, Serializable {
  
  public void setSearchCommand(Pazpar2Command command);
  public HttpResponseWrapper executeCommand(Pazpar2Command command);
  
  // Use cloneMe() method if injecting the client with CDI.
  // The client is used for asynchronously sending off requests
  // to the server AND propagation of context to threads is currently 
  // not supported. Trying to do so throws a WELD-001303 error. 
  // If propagation to threads gets supported, the cloning can go.
  public SearchClient cloneMe();
  
  public boolean isAuthenticatingClient();  
  public Configuration getConfiguration();
  public String getServiceUrl();
  public void setServiceUrl(String url);
  public boolean hasServiceUrl();  
}
