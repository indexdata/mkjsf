package com.indexdata.pz2utils4jsf.errors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2Responses;

@Named("errors") @SessionScoped
public class ErrorCentral implements Serializable {

  private static final long serialVersionUID = -1658192041068396628L;
  private static Logger logger = Logger.getLogger(ErrorCentral.class);  
  private ErrorHelper errorHelper = null;
  
  @Inject Pazpar2Responses pzresp;
  @Inject ConfigurationReader configurator;
  
  private List<ErrorInterface> configurationErrors = new ArrayList<ErrorInterface>();

  public ErrorCentral() {}
  
  @PostConstruct 
  public void postConstruct() {
    errorHelper = new ErrorHelper(configurator);
    pzresp.setErrorHelper(errorHelper);    
  }
    
  public void addConfigurationError (ErrorInterface configError) {
    configError.setErrorHelper(errorHelper);
    configurationErrors.add(configError);
  }
  
  public boolean hasConfigurationErrors () {
    return (configurationErrors.size()>0);      
  }

  public boolean hasCommandErrors () {
    return pzresp.hasApplicationError();
  }
  
  public ErrorInterface getCommandError () {
    return pzresp.getCommandError();
  }

  /**
   * Returns true if application error found in any response data objects 
   */
  public boolean hasErrors () {
    logger.debug("Checking for configuration errors or command errors.");
    return hasConfigurationErrors() || hasCommandErrors();
  }
  
  public List<ErrorInterface> getConfigurationErrors() {    
    return configurationErrors;
  }


}
