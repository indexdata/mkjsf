package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.IOException;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.indexdata.mkjsf.utils.FileUpload;
import com.indexdata.mkjsf.utils.Utils;

/**
 * Helper class for file upload of an Service Proxy init doc.
 * 
 * @author Niels Erik
 *
 */
@Named
@SessionScoped
public class InitDocUpload extends FileUpload {

  
  private UploadedFile uploadedFile = null;
  private static Logger logger = Logger.getLogger(InitDocUpload.class);
  private static final long serialVersionUID = 1846749236304941323L;  
  
  public InitDocUpload() {    
  }
    
  public UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  public void setUploadedFile(UploadedFile uploadedFile) {
    logger.info(Utils.objectId(this) + " received an uploaded file [" + Utils.objectId(uploadedFile) + "]");
    this.uploadedFile = uploadedFile;
    try {
      logger.info("File length: " + this.uploadedFile.getBytes().length);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public boolean hasUploadedFile () {
    return uploadedFile != null;
  }

}
