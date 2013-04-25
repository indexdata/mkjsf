package com.indexdata.mkjsf.pazpar2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.pazpar2.sp.ServiceProxyClient;
import com.indexdata.mkjsf.pazpar2.sp.ServiceProxyInterface;
import com.indexdata.mkjsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.mkjsf.utils.Utils;

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
      stateMgr.addStateListener(this);
      serviceProxyUrl = searchClient.getConfiguration().get(ServiceProxyClient.SERVICE_PROXY_URL);
    } else {
      logger.debug("Pz2ProxyBean:postConstruct: searchClient already instantiated " +
      		        "during construction of parent object Pz2Bean.");
    }
  }
  
  public void login(String un, String pw) {
    if (user.isAuthenticated() && user.getName().equals(un) && ((ServiceProxyClient) searchClient).checkAuthentication(user)) {
      logger.info("Repeat request from UI to authenticate user. Auth verified for given user name so skipping log-in.");
    } else {
      logger.info("doing un/pw login");
      user.setName(un);
      user.setPassword(pw);
      login("dummy");
    }
  }

  @Override
  public String login(String navigateTo) {
    logger.info("doing login");
    ((ServiceProxyClient)searchClient).authenticate(user);    
    pzreq.getRecord().removeParametersInState();
    pzreq.getSearch().removeParametersInState();
    pzresp.reset();
    return navigateTo;
  }
  
  public void ipAuthenticate (ServiceProxyUser user) {
    if (!user.isAuthenticated()) {
      ((ServiceProxyClient)searchClient).ipAuthenticate(user);
    }
  }

  @Override
  public void setServiceProxyUrl(String url) {
    logger.info("Setting Service Proxy url: " + url);
    serviceProxyUrl = url;
    pzreq.getRecord().removeParametersInState();
    pzreq.getSearch().removeParametersInState();
    pzresp.reset();
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
    pzresp.reset();
    byte[] response = ((ServiceProxyClient)searchClient).postInitDoc(initDocPath + getInitFileName());
    initDocResponse = new String(response,"UTF-8");
    return initDocResponse;
  }
  
  @Override
  public String postInit(byte[] initDoc) throws UnsupportedEncodingException, IOException {    
    pzresp.reset();
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