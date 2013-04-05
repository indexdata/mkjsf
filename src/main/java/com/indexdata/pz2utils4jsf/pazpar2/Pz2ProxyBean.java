package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.Configuration;
import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.pazpar2.sp.ServiceProxyClient;
import com.indexdata.pz2utils4jsf.pazpar2.sp.ServiceProxyInterface;
import com.indexdata.pz2utils4jsf.pazpar2.sp.ServiceProxySession;
import com.indexdata.pz2utils4jsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named("pz2") @SessionScoped @Alternative
public class Pz2ProxyBean extends Pz2Bean implements ServiceProxyInterface {
    
  private static final long serialVersionUID = 4221824985678758225L;
  private static Logger logger = Logger.getLogger(Pz2ProxyBean.class);  
    
  @Inject ConfigurationReader configurator;
  @Inject ServiceProxyUser user;
  
  public Pz2ProxyBean() {
  }
  
  @PostConstruct
  public void instantiatePz2SessionObject() {
    logger.debug(Utils.objectId(this) + " will instantiate a Pz2Session object next.");
    pz2 = new ServiceProxySession();
    searchClient = new ServiceProxyClient();
    logger.info("Using [" + Utils.objectId(searchClient) + "] configured by [" 
                          + Utils.objectId(configurator) + "] on session [" 
                          + Utils.objectId(pz2) + "]" );    
    pz2.configureClient(searchClient,configurator);
  }

  @Override
  public String login(String navigateTo) {
    logger.info("doing login");
    ((ServiceProxySession) pz2).setUser(user);
    return session().login(navigateTo);
  }

  @Override
  public void setInitFileName(String fileName) {
    logger.info("Setting init file name: " + fileName);
    session().setInitFileName(fileName);      
  }

  @Override
  public String getInitFileName() {
    return session().getInitFileName();
  }
  
  public void setAceFilter (String filterExpression) {
    session().setAceFilter(filterExpression);
  }
  
  public String getAceFilter () {
    return session().getAceFilter();
  }

  @Override
  public String postInit() throws UnsupportedEncodingException, IOException {
    logger.info("Posting init: " + System.currentTimeMillis());
    session().postInit();
    return "";
  }
  
  public String postInit(byte[] initDoc) throws UnsupportedEncodingException, IOException {
    logger.info("Posting init: " + System.currentTimeMillis());
    session().postInit(initDoc);
    return "";
    
  }

  @Override
  public void setServiceProxyUrl(String url) {
    logger.info("Setting Service Proxy url: " + url);
    session().setServiceProxyUrl(url); 
    session().setQuery(null);
    session().resetDataObjects();
  }

  @Override
  public String getServiceProxyUrl() {
    return session().getServiceProxyUrl();    
  }
  
  public ServiceProxySession session() {
    return (ServiceProxySession)pz2;
  }

  @Override
  public String getInitResponse() {
    return session().getInitResponse();
  }
  
  public Configuration getClientConfiguration() {
    return session().client().getConfiguration();
  }
  
  public String getInitDocPath () {
    return session().client().getConfiguration().get("INIT_DOC_PATH");
  }

}
