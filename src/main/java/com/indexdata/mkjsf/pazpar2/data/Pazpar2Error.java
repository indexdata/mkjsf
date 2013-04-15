package com.indexdata.mkjsf.pazpar2.data;

public class Pazpar2Error extends Pazpar2ResponseData {

  private static final long serialVersionUID = -7060267782024414318L;

  public String getCode() {
    return getAttribute("code");
  }
  
  public String getMsg() {
    return getAttribute("msg");
  }
}
