package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.IOException;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.ServiceProxyExtensions;
import com.indexdata.mkjsf.utils.FileUpload;
import com.indexdata.mkjsf.utils.Utils;

@Named
@SessionScoped
public class InitDocUpload extends FileUpload {

  private static Logger logger = Logger.getLogger(InitDocUpload.class);
  private static final long serialVersionUID = 1846749236304941323L;  
  private boolean includeDebug = false;
  private ServiceProxyExtensions spBean;
  
  public InitDocUpload(ServiceProxyExtensions spBean) {
    this.spBean = spBean;
  }
  
  public String submit() throws IOException {
    logger.info(Utils.objectId(this) + " submitting");
    //String fileName = FilenameUtils.getName(uploadedFile.getName());
    //String contentType = uploadedFile.getContentType();
    byte[] bytes = uploadedFile.getBytes();
    response = spBean.postInit(bytes, includeDebug);
    return "";
  }

  public void setIncludeDebug(boolean bool) {
    logger.info(Utils.objectId(this) + " setting debug to " + bool);
    includeDebug = bool;
  }

  public boolean getIncludeDebug() {
    return includeDebug;
  }

}
