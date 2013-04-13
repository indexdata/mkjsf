package com.indexdata.pz2utils4jsf.pazpar2.sp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.ForServiceProxy;
import com.indexdata.pz2utils4jsf.pazpar2.Pz2Session;
import com.indexdata.pz2utils4jsf.pazpar2.commands.CommandParameter;
import com.indexdata.pz2utils4jsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.pz2utils4jsf.utils.Utils;

@ForServiceProxy
public class ServiceProxySession extends Pz2Session implements ServiceProxyInterface {

  private ServiceProxyUser user; 
  private static final long serialVersionUID = -5770410029361522854L;
  private static Logger logger = Logger.getLogger(ServiceProxySession.class);
  private String initDocFileName = "";
  private String initDocResponse = "";  
    
  public ServiceProxySession() {
    logger.info("Instantiating pz2 session object [" + Utils.objectId(this) + "]");
  }
  
  public void setUser(ServiceProxyUser user) {
    this.user = user;
  }

  @Override
  public String login(String navigateTo) {
    if (client().authenticate(user)) {
      return navigateTo;
    } else {
      return null;
    }      
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
    String initDocPath = client().getInitDocPaths()[0];
    logger.info("Paths: " + client().getInitDocPaths());
    logger.info("Path: " + initDocPath);
    this.resetDataObjects();
    byte[] response = client().postInitDoc(initDocPath + getInitFileName());
    initDocResponse = new String(response,"UTF-8");
    return initDocResponse;
  }
  
  @Override
  public String postInit(byte[] initDoc) throws UnsupportedEncodingException, IOException {    
    this.resetDataObjects();
    byte[] response = client().postInitDoc(initDoc);
    initDocResponse = new String(response,"UTF-8");
    return initDocResponse;
  }


  @Override
  public void setServiceProxyUrl(String url) {
    client().setServiceProxyUrl(url);
    
  }

  @Override
  public String getServiceProxyUrl() {
    return client().getServiceProxyUrl();
  }
  
  public ServiceProxyClient client () {
    return (ServiceProxyClient)searchClient;
  }

  @Override
  public String getInitResponse() {
    return initDocResponse;
  }
  
  public void setAceFilter(String filterExpression) {
    setCommandParameter("record",new CommandParameter("acefilter","=",filterExpression));
  }
  
  public String getAceFilter () {
    return getCommandParameterValue("record","acefilter","");
  }
  
}
