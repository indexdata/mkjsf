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


  protected HttpResponseWrapper response;
  private static Logger logger = Logger.getLogger(FileUpload.class);
  private static final long serialVersionUID = 748784638056392862L;

  public FileUpload() {
  }

  public abstract HttpResponseWrapper submit() throws IOException;
  
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
