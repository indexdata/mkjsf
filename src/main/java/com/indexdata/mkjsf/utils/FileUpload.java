package com.indexdata.mkjsf.utils;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.indexdata.mkjsf.pazpar2.HttpResponseWrapper;

public abstract class FileUpload implements Serializable {

  protected UploadedFile uploadedFile;
  protected HttpResponseWrapper response;
  private static Logger logger = Logger.getLogger(FileUpload.class);
  private static final long serialVersionUID = 748784638056392862L;

  public FileUpload() {
  }

  public abstract String submit() throws IOException;
  
  public UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  public void setUploadedFile(UploadedFile uploadedFile) {
    logger.info(Utils.objectId(this) + " received an uploaded file [" + Utils.objectId(uploadedFile) + "]");
    this.uploadedFile = uploadedFile;
  }
  
  public void downloadDoc() throws IOException {
    logger.info(Utils.objectId(this) + " got a download request");
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    externalContext.setResponseHeader("Content-Type", uploadedFile.getContentType());
    externalContext.setResponseHeader("Content-Length", String.valueOf((uploadedFile.getBytes().length)));
    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + FilenameUtils.getBaseName(uploadedFile.getName()) + "\"");
    externalContext.getResponseOutputStream().write(uploadedFile.getBytes());
    facesContext.responseComplete();
  }
  
  public void downloadResponse () throws IOException {
    logger.info(Utils.objectId(this) + " got a download request");
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    externalContext.setResponseHeader("Content-Type", response.getContentType());
    externalContext.setResponseHeader("Content-Length", String.valueOf((response.getBytes().length)));
    externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"initresponse.xml\"");
    externalContext.getResponseOutputStream().write(response.getBytes());
    facesContext.responseComplete();
  }



}
