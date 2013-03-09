package com.indexdata.pz2utils4jsf.errors;

import java.io.Serializable;
import java.util.List;

import com.indexdata.pz2utils4jsf.errors.ErrorHelper.ErrorCode;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2Error;


public interface ApplicationError extends Serializable {
  
  public String getLabel();
  public String getMessage(); 
  public String getException();
  public void setApplicationErrorCode(ErrorCode code);
  public ErrorCode getApplicationErrorCode();
  public List<String> getSuggestions();
  public void setErrorHelper(ErrorHelper helper);
  public boolean hasPazpar2Error();
  public Pazpar2Error getPazpar2Error();
    
}
