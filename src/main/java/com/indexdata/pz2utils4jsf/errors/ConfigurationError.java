package com.indexdata.pz2utils4jsf.errors;

import java.util.List;

import com.indexdata.pz2utils4jsf.errors.ErrorHelper.ErrorCode;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2Error;


public class ConfigurationError implements ApplicationError {

  private static final long serialVersionUID = -6599667223782130838L;
  private String label;
  private String message;
  private String exception;
  private ErrorHelper helper;
  private ErrorCode applicationErrorCode;
  
  public ConfigurationError(String label, String exception, String message, ErrorHelper helper) {
    this.label = label;
    this.message = message;
    this.helper = helper;  
    this.exception = exception;
  }
  
  public List<String> getSuggestions() {
    return helper.getSuggestions(this);
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getMessage() {
    return message;
  }
  
  @Override
  public String getException() {
    return exception;
  }
  
  @Override
  public void setErrorHelper (ErrorHelper helper) {
    this.helper = helper;
  }

  @Override
  public void setApplicationErrorCode(ErrorCode code) {
    this.applicationErrorCode = code;
  }

  @Override
  public ErrorCode getApplicationErrorCode() {
    return applicationErrorCode;
  }
  
  public boolean hasPazpar2Error () {
    return false;
  }
  
  public Pazpar2Error getPazpar2Error() {
    return null;
  }
 
}
