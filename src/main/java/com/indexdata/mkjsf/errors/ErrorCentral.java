package com.indexdata.mkjsf.errors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.pazpar2.Pz2Bean;

@SessionScoped @Named
public class ErrorCentral implements Serializable {

  private static final long serialVersionUID = -1658192041068396628L;
  private static Logger logger = Logger.getLogger(ErrorCentral.class);  
  private ErrorHelper errorHelper = null;
    
  @Inject ConfigurationReader configurator;  
  
  private List<ErrorInterface> configurationErrors = new ArrayList<ErrorInterface>();

  public ErrorCentral() {
    logger.info("Instantiating ErrorCentral "+this);
    errorHelper = new ErrorHelper(configurator);       
  }
      
  public void addConfigurationError (ErrorInterface configError) {
    configError.setErrorHelper(errorHelper);
    configurationErrors.add(configError);
  }
  
  public boolean hasConfigurationErrors () {
    return (configurationErrors.size()>0);      
  }

  public boolean hasCommandErrors () {
    return Pz2Bean.get().getPzresp().hasApplicationError();
  }
  
  public ErrorInterface getCommandError () {
    return Pz2Bean.get().getPzresp().getCommandError();
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
  
  public ErrorHelper getHelper () {
    return errorHelper;
  }


}
