package com.indexdata.mkjsf.errors;

import java.io.Serializable;
import java.util.List;

import com.indexdata.mkjsf.errors.ErrorHelper.ErrorCode;
import com.indexdata.mkjsf.pazpar2.data.ServiceError;


public interface ErrorInterface extends Serializable {
  
  public String getLabel();
  public String getMessage(); 
  public String getException();
  public void setApplicationErrorCode(ErrorCode code);
  public ErrorCode getApplicationErrorCode();
  public List<String> getSuggestions();
  public void setErrorHelper(ErrorHelper helper);
  public boolean isServiceError();
  public ServiceError getServiceError();
    
}
