package com.indexdata.mkjsf.errors;

import java.util.List;

import com.indexdata.mkjsf.errors.ErrorHelper.ErrorCode;
import com.indexdata.mkjsf.pazpar2.data.Pazpar2Error;


public class ConfigurationError implements ErrorInterface {

  private static final long serialVersionUID = -6599667223782130838L;
  private String label;
  private String message;
  private String exception;
  private ErrorHelper helper;
  private ErrorCode applicationErrorCode;
  
  public ConfigurationError(String label, String exception, String message) {
    this.label = label;
    this.message = message;    
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
