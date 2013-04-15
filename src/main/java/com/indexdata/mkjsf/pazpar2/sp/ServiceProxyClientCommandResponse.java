package com.indexdata.mkjsf.pazpar2.sp;

import com.indexdata.mkjsf.pazpar2.CommandResponse;

public class ServiceProxyClientCommandResponse implements CommandResponse {

  private int statusCode = 0;
  private String content = null;
  
  public ServiceProxyClientCommandResponse(int statusCode, String content) {
    this.statusCode = statusCode;
    this.content = content;
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String getContentType() {
    return "text/xml;charset=UTF-8";    
  }

  @Override
  public String getResponseString() {
    return content;
  }

}
