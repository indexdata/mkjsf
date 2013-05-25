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
import com.indexdata.mkjsf.pazpar2.commands.sp.InitDocUpload;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;
import com.indexdata.mkjsf.utils.Utils;

@Named("pz2x") @SessionScoped
public class ServiceProxyExtensions implements ServiceProxyInterface, Serializable {
    
  private static final long serialVersionUID = 4221824985678758225L;
  private static Logger logger = Logger.getLogger(ServiceProxyExtensions.class);  
  private String initDocFileName = "";
  private String initDocResponse = "";  
  private InitDocUpload initDocUpload; 
    
  @Inject ConfigurationReader configurator;    
    
  public ServiceProxyExtensions() {
    this.initDocUpload = new InitDocUpload(this);
    // TODO: 
    //stateMgr.addStateListener(this);
  }
           
  public String getInitDocPath () {
    return Pz2Bean.get().getSpClient().getConfiguration().get("INIT_DOC_PATH");
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
    String initDocPath = Pz2Bean.get().getSpClient().getInitDocPaths().get(0);
    logger.info("Paths: " + Pz2Bean.get().getSpClient().getInitDocPaths());
    logger.info("Path: " + initDocPath);   
    Pz2Bean.get().resetSearchAndRecordCommands();
    Pz2Bean.get().getPzresp().getSp().resetAuthAndBeyond(true);
    ClientCommandResponse response = Pz2Bean.get().getSpClient().postInitDoc(initDocPath + getInitFileName());    
    return response;
  }
  
  @Override
  public HttpResponseWrapper postInit(byte[] initDoc, boolean includeDebug) throws UnsupportedEncodingException, IOException {    
    Pz2Bean.get().resetSearchAndRecordCommands();
    Pz2Bean.get().getPzresp().getSp().resetAuthAndBeyond(true);
    HttpResponseWrapper response = Pz2Bean.get().getSpClient().postInitDoc(initDoc,includeDebug);    
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
    Pz2Bean.get().getPzresp().put("init", responseObject);
  }
  
  public void setIncludeInitDocDebug(boolean bool) {
    logger.info(Utils.objectId(this) + " setting debug to " + bool);
    initDocUpload.setIncludeDebug(bool);
  }

  public boolean getIncludeInitDocDebug() {
    return initDocUpload.getIncludeDebug();
  }
  
  // TODO: Remove when obsolete
  public InitDocUpload getInitDocUpload () {
    return initDocUpload;
  }
  
   
}
