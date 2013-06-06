package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.indexdata.mkjsf.pazpar2.ClientCommandResponse;
import com.indexdata.mkjsf.pazpar2.HttpResponseWrapper;
import com.indexdata.mkjsf.pazpar2.Pz2Service;
import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.InitCommand;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.ResponseParser;
import com.indexdata.mkjsf.pazpar2.data.sp.SpResponseDataObject;

/**
 * Service Proxy extensions to the init command - specifically 
 * support for POSTing to the Service Proxy an init doc containing Pazpar2 
 * definitions and settings. 
 * 
 * @author Niels Erik
 *
 */
public class InitCommandSp implements Serializable, ServiceProxyCommand {

  private static final long serialVersionUID = -6609045941782375651L;
  private static Logger logger = Logger.getLogger(InitCommandSp.class);
  private InitCommand command = null;
  
  private InitDocUpload initDocUpload;

  public InitCommandSp(InitCommand initCommand) {
    this.command=initCommand;
    initDocUpload = new InitDocUpload();
  }
  
  public void setIncludeDebug (String includeDebug) {
    command.setParameterInState(new CommandParameter("includeDebug","=",includeDebug));
  }
  
  public String getIncludeDebug () {
    return command.getParameterValue("includeDebug");
  }

  public void setUploadedInitDoc (UploadedFile uploadedFile) {
    initDocUpload.setUploadedFile(uploadedFile);
  }
  
  public UploadedFile getUploadedInitDoc () {
    logger.info("initDocUpload is: " + initDocUpload );
    logger.info("initDocUpload.getUploadedFile() is: " +initDocUpload.getUploadedFile());
    return initDocUpload.getUploadedFile();
  }
  
  public void downloadInitDoc () throws IOException {
    initDocUpload.downloadDoc();
  }
  
  public SpResponseDataObject run() {
    Pz2Service.get().resetSearchAndRecordCommands();
    Pz2Service.get().getPzresp().getSp().resetAuthAndBeyond(true);    
    try {
      byte[] bytes = getUploadedInitDoc().getBytes();
      HttpResponseWrapper response = Pz2Service.get().getSpClient().postInitDoc(bytes,getIncludeDebug().equals("yes"));    
      ResponseDataObject responseObject = ResponseParser.getParser().getDataObject((ClientCommandResponse)response);    
      Pz2Service.get().getPzresp().put("init", responseObject);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean spOnly() {
    return true;
  }
  
  
  
}
