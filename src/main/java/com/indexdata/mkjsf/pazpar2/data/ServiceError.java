package com.indexdata.mkjsf.pazpar2.data;

public class ServiceError extends ResponseDataObject {

  private static final long serialVersionUID = -7060267782024414318L;

  public String getCode() {
    return getAttribute("code");
  }
  
  public String getMsg() {
    return getAttribute("msg");
  }
  
  public boolean isPazpar2Error() {
    return (getCode() != null && getCode().length()>0 && Integer.parseInt(getCode())<100);
  }
  
  public boolean isServiceProxyError() {
    return (getCode() != null && getCode().length()>0 && Integer.parseInt(getCode())>100);
  }        
}
