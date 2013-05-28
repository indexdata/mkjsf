package com.indexdata.mkjsf.utils;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

public abstract class FileUpload implements Serializable {

  private static Logger logger = Logger.getLogger(FileUpload.class);
  private static final long serialVersionUID = 748784638056392862L;

  public FileUpload() {
  }

  public abstract UploadedFile getUploadedFile();
  
  public abstract void setUploadedFile(UploadedFile uploadedFile);
  
  public void downloadDoc() throws IOException {
    logger.info(Utils.objectId(this) + " got a download request");
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    externalContext.setResponseHeader("Content-Type", getUploadedFile().getContentType());
    externalContext.setResponseHeader("Content-Length", String.valueOf((getUploadedFile().getBytes().length)));
    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + FilenameUtils.getBaseName(getUploadedFile().getName()) + "\"");
    externalContext.getResponseOutputStream().write(getUploadedFile().getBytes());
    facesContext.responseComplete();
  }

}
