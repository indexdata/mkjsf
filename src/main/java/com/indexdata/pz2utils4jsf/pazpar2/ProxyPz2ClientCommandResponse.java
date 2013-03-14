package com.indexdata.pz2utils4jsf.pazpar2;

public class ProxyPz2ClientCommandResponse implements CommandResponse {

  private int statusCode = 0;
  private String content = null;
  
  public ProxyPz2ClientCommandResponse(int statusCode, String content) {
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
