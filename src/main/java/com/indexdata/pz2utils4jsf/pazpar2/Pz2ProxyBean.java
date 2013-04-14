package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.pazpar2.sp.ServiceProxyClient;
import com.indexdata.pz2utils4jsf.pazpar2.sp.ServiceProxyInterface;
import com.indexdata.pz2utils4jsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named("pz2") @SessionScoped @Alternative
public class Pz2ProxyBean extends Pz2Bean implements ServiceProxyInterface {
    
  private static final long serialVersionUID = 4221824985678758225L;
  private static Logger logger = Logger.getLogger(Pz2ProxyBean.class);  
  private String initDocFileName = "";
  private String initDocResponse = "";
  private String serviceProxyUrl = "";  
    
  @Inject ConfigurationReader configurator;
  @Inject ServiceProxyUser user;    
  
  public Pz2ProxyBean() {
  }
  
  @PostConstruct
  public void postConstruct() {
    if (searchClient == null) {
      logger.debug(Utils.objectId(this) + " will instantiate a ServiceProxyClient next.");    
      searchClient = new ServiceProxyClient();
      logger.info("Using [" + Utils.objectId(searchClient) + "] configured by [" 
                            + Utils.objectId(configurator) + "]" );    
      configureClient(searchClient,configurator);
    } else {
      logger.info("Pz2ProxyBean:postConstruct: searchClient already instantiated " +
      		        "during construction of parent object Pz2Bean.");
    }
  }

  @Override
  public String login(String navigateTo) {
    logger.info("doing login");
    ((ServiceProxyClient)searchClient).authenticate(user);
    data.reset();
    req.getRecord().removeParameters();
    req.getSearch().setQuery(null);
    
    return navigateTo;
  }

  @Override
  public void setServiceProxyUrl(String url) {
    logger.info("Setting Service Proxy url: " + url);
    serviceProxyUrl = url;
    req.getSearch().setQuery(null);
    data.reset();
  }
  
  public String getServiceProxyUrl() {
    return serviceProxyUrl;
  }
    
  public String getInitDocPath () {
    return searchClient.getConfiguration().get("INIT_DOC_PATH");
  }
  
  @Override
  public void setInitFileName(String fileName) {
    this.initDocFileName = fileName;
    
  }

  @Override
  public String getInitFileName() {
    return initDocFileName;
  }

  @Override
  public String postInit() throws UnsupportedEncodingException, IOException {    
    String initDocPath = ((ServiceProxyClient)searchClient).getInitDocPaths()[0];
    logger.info("Paths: " + ((ServiceProxyClient)searchClient).getInitDocPaths());
    logger.info("Path: " + initDocPath);
    data.reset();
    byte[] response = ((ServiceProxyClient)searchClient).postInitDoc(initDocPath + getInitFileName());
    initDocResponse = new String(response,"UTF-8");
    return initDocResponse;
  }
  
  @Override
  public String postInit(byte[] initDoc) throws UnsupportedEncodingException, IOException {    
    data.reset();
    byte[] response = ((ServiceProxyClient)searchClient).postInitDoc(initDoc);
    initDocResponse = new String(response,"UTF-8");
    return initDocResponse;
  }


  @Override
  public String getInitResponse() {
    return initDocResponse;
  }
  
  public void setAceFilter(String filterExpression) {
    //setCommandParameter("record",new CommandParameter("acefilter","=",filterExpression));
  }
  
  public String getAceFilter () {
    return null;
    // return getCommandParameterValue("record","acefilter","");
  }


}
