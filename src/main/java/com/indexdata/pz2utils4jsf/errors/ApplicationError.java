package com.indexdata.pz2utils4jsf.errors;

import java.io.Serializable;
import java.util.List;

import com.indexdata.pz2utils4jsf.errors.ErrorHelper.ErrorCode;


public interface ApplicationError extends Serializable {
  
  public String getLabel();
  public String getMessage(); 
  public String getException();
  public void setApplicationErrorCode(ErrorCode code);
  public ErrorCode getApplicationErrorCode();
  public List<String> getSuggestions();
  public void setErrorHelper(ErrorHelper helper);
    
}
