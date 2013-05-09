package com.indexdata.mkjsf.pazpar2;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.indexdata.mkjsf.config.ConfigurationReader;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Commands;
import com.indexdata.mkjsf.pazpar2.commands.sp.InitDocUpload;
import com.indexdata.mkjsf.pazpar2.data.InitResponse;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;
import com.indexdata.mkjsf.pazpar2.data.Responses;
import com.indexdata.mkjsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.mkjsf.utils.Utils;

@Named("pz2x") @SessionScoped
public class ServiceProxyExtensions implements ServiceProxyInterface, Serializable {
    
  private static final long serialVersionUID = 4221824985678758225L;
  private static Logger logger = Logger.getLogger(ServiceProxyExtensions.class);  
  private String initDocFileName = "";
  private String initDocResponse = "";  
  private InitDocUpload initDocUpload; 
    
  @Inject ConfigurationReader configurator;  
  @Inject ServiceProxyUser user;    
  @Inject Pz2Bean pz2;
  @Inject Pazpar2Commands pzreq;
  @Inject Responses pzresp;

  
  public ServiceProxyExtensions() {
    this.initDocUpload = new InitDocUpload(this);
    // TODO: 
    //stateMgr.addStateListener(this);
  }
    
  public void login(String un, String pw) {
    if (user.isAuthenticated() && user.getName().equals(un) && pz2.spClient.checkAuthentication(user)) {
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
    logger.info("doing login by " + user + " using " + pz2 + " and client " + pz2.getSpClient());
    pz2.resetSearchAndRecordCommands();
    pzresp.resetAllSessionData();
    pz2.getSpClient().authenticate(user);    
    return navigateTo;
  }
  
  public void ipAuthenticate (ServiceProxyUser user) {
    if (!user.isIpAuthenticated()) {
      if (user.isAuthenticated()) {
        user.clear();
      }
      pz2.resetSearchAndRecordCommands();
      pzresp.resetAllSessionData();
      pz2.getSpClient().ipAuthenticate(user);
    }
  }
    
  public String getInitDocPath () {
    return pz2.getSpClient().getConfiguration().get("INIT_DOC_PATH");
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
  public ClientCommandResponse postInit() throws UnsupportedEncodingException, IOException {    
    String initDocPath = pz2.getSpClient().getInitDocPaths().get(0);
    logger.info("Paths: " + pz2.getSpClient().getInitDocPaths());
    logger.info("Path: " + initDocPath);
    pz2.resetSearchAndRecordCommands();
    pzresp.resetAllSessionData();
    ClientCommandResponse response = pz2.getSpClient().postInitDoc(initDocPath + getInitFileName());    
    return response;
  }
  
  @Override
  public ClientCommandResponse postInit(byte[] initDoc, boolean includeDebug) throws UnsupportedEncodingException, IOException {    
    pz2.resetSearchAndRecordCommands();
    pzresp.resetAllSessionData();
    ClientCommandResponse response = pz2.getSpClient().postInitDoc(initDoc,includeDebug);    
    return response;
  }

  @Override
  public String getInitResponse() {
    return initDocResponse;
  }
  
  public void setUploadedInitDoc (UploadedFile uploadedFile) {
    initDocUpload.setUploadedFile(uploadedFile);
  }
  
  public UploadedFile getUploadedInitDoc () {
    return initDocUpload.getUploadedFile();
  }
  
  public void submitInitDoc () throws IOException {
    HttpResponseWrapper response =  initDocUpload.submit();
    ResponseDataObject responseObject = ResponseParser.getParser().getDataObject(response.getResponseString());
    logger.info("Putting init response to : " + Utils.objectId(pzresp));
    pzresp.put("init", responseObject);
  }
  
  public void setIncludeInitDocDebug(boolean bool) {
    logger.info(Utils.objectId(this) + " setting debug to " + bool);
    initDocUpload.setIncludeDebug(bool);
  }

  public boolean getIncludeInitDocDebug() {
    return initDocUpload.getIncludeDebug();
  }
  
  // TODO: Remove when possible
  public InitDocUpload getInitDocUpload () {
    return initDocUpload;
  }

  
  
  
}
