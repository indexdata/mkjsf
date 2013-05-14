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
import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Commands;
import com.indexdata.mkjsf.pazpar2.commands.sp.AuthCommand;
import com.indexdata.mkjsf.pazpar2.commands.sp.InitDocUpload;
import com.indexdata.mkjsf.pazpar2.data.AuthResponse;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;
import com.indexdata.mkjsf.pazpar2.data.Responses;
import com.indexdata.mkjsf.pazpar2.data.sp.CategoriesResponse;
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
   
  /*
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
  */

  
  @Override  
  public String login(String navigateTo) {
    logger.info("doing login by " + user + " using " + pz2 + " and client " + pz2.getSpClient());
    pz2.resetSearchAndRecordCommands();
    pzresp.resetAllSessionData();
    AuthCommand auth = pzreq.getSp().getAuth(); 
    auth.setParametersInState(new CommandParameter("action","=","login"),
                              new CommandParameter("username","=",user.getName()),
                              new CommandParameter("password","=",user.getPassword()));
    ClientCommandResponse commandResponse = pz2.getSpClient().send(auth);
    String renamedResponse = renameResponseElement(commandResponse.getResponseString(), "auth");
    commandResponse.setResponseToParse(renamedResponse);
    AuthResponse responseObject = (AuthResponse) ResponseParser.getParser().getDataObject(commandResponse);
    if (ResponseParser.docTypes.contains(responseObject.getType())) {
      pzresp.put(auth.getCommandName(), responseObject);
    }
    String responseStr = commandResponse.getResponseString();
    logger.info(responseStr);      
    if (responseStr.contains("FAIL")) {
      user.credentialsAuthenticationSucceeded(false);    
    } else {
      user.credentialsAuthenticationSucceeded(true);    
    }      
    return navigateTo;
  }
  
  
  public void ipAuthenticate (ServiceProxyUser user) {
    if (!user.isIpAuthenticated()) {
      if (user.isAuthenticated()) {
        user.clear();
      }
      pz2.resetSearchAndRecordCommands();
      pzresp.resetAllSessionData();
      AuthCommand auth = pzreq.getSp().getAuth(); 
      auth.setParameterInState(new CommandParameter("action","=","ipAuth"));
      ClientCommandResponse commandResponse = pz2.getSpClient().send(auth);      
      String renamedResponse = renameResponseElement(commandResponse.getResponseString(), "auth");
      commandResponse.setResponseToParse(renamedResponse);
      ResponseDataObject responseObject = ResponseParser.getParser().getDataObject(commandResponse);
      if (ResponseParser.docTypes.contains(responseObject.getType())) {
        pzresp.put(auth.getCommandName(), responseObject);
      }
      String responseStr = commandResponse.getResponseString();
      logger.info(responseStr);      
      if (responseStr.contains("FAIL")) {
        user.credentialsAuthenticationSucceeded(false);    
      } else {
        user.credentialsAuthenticationSucceeded(true);    
      }      
    }
  }
  
  private String renameResponseElement(String responseString, String newName) {
    responseString = responseString.replace("<response>", "<" + newName + ">");
    responseString = responseString.replace("</response>", "</" + newName + ">");
    return responseString;
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
  public HttpResponseWrapper postInit(byte[] initDoc, boolean includeDebug) throws UnsupportedEncodingException, IOException {    
    pz2.resetSearchAndRecordCommands();
    pzresp.resetAllSessionData();
    HttpResponseWrapper response = pz2.getSpClient().postInitDoc(initDoc,includeDebug);    
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
    ClientCommandResponse response =  (ClientCommandResponse) initDocUpload.submit();
    ResponseDataObject responseObject = ResponseParser.getParser().getDataObject(response);
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
  
  public CategoriesResponse getCategories () {
    ResponseDataObject response = pz2.doCommand("categories");
    if (response.hasApplicationError()) {
      logger.debug(response.getXml());
      return new CategoriesResponse();
    } else {
      try {
        return (CategoriesResponse) response;
      } catch (Exception e) {
        e.printStackTrace();
        logger.debug(response.getXml());
        return new CategoriesResponse();
      }
    }
  }
  
  
}