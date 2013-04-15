package com.indexdata.mkjsf.errors;

import java.io.Serializable;
import java.util.List;

import com.indexdata.mkjsf.errors.ErrorHelper.ErrorCode;
import com.indexdata.mkjsf.pazpar2.data.Pazpar2Error;


public interface ErrorInterface extends Serializable {
  
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
