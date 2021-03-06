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
 * Service Proxy extensions to the Pazpar2 <code>init</code> command - specifically 
 * support for POSTing to the Service Proxy an init doc containing Pazpar2 
 * definitions and settings. 
 * 
 * This feature, however, is not supported in the publicly released Service Proxy at this point. 
 * 
 * @author Niels Erik
 *
 */
public class InitCommandSp implements Serializable, ServiceProxyCommand {

  private static final long serialVersionUID = -6609045941782375651L;
  private static Logger logger = Logger.getLogger(InitCommandSp.class);
  private InitCommand command = null;
  
  private InitDocUpload initDocUpload = null;

  public InitCommandSp(InitCommand initCommand) {
    this.command=initCommand;
    initDocUpload = new InitDocUpload();
  }
  
  /**
   * Sets Service Proxy command parameter <code>includeDebug</code>.  
   */
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
    if (initDocUpload != null) {
      logger.info("initDocUpload.getUploadedFile() is: " +initDocUpload.getUploadedFile());    
    return initDocUpload.getUploadedFile();
    } else {
      return null;
    }
  }
  
  public void downloadInitDoc () throws IOException {
    initDocUpload.downloadDoc();
  }
  
  public SpResponseDataObject run() {
    Pz2Service.get().resetSearchAndRecordCommands();
    Pz2Service.get().getPzresp().getSp().resetAuthAndBeyond(true);    
    if (initDocUpload.hasUploadedFile()) {
      try {
        byte[] bytes = getUploadedInitDoc().getBytes();
        HttpResponseWrapper response = Pz2Service.get().getSpClient().postInitDoc(bytes,command);    
        ResponseDataObject responseObject = ResponseParser.getParser().getDataObject((ClientCommandResponse)response);    
        Pz2Service.get().getPzresp().put("init", responseObject);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      Pz2Service.get().getSpClient().executeCommand(this.command);
    }
    return null;
  }
  
  /**
   * Sets the <code>windowid</code> parameter. See Service Proxy documentation for details.
   */  
  public void setWindowid (String windowid) {
    command.setParameterInState(new CommandParameter("windowid","=",windowid));
  }
  
  /** 
   * Returns the <code>windowid</code> parameter value.
   */
  public String getWindowid () {
    return command.getParameterValue("windowid");
  }

  @Override
  public boolean spOnly() {
    return true;
  }  
  
}
