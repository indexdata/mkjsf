package com.indexdata.mkjsf.pazpar2.sp;

import java.io.UnsupportedEncodingException;

import com.indexdata.mkjsf.pazpar2.CommandResponse;

public class ServiceProxyCommandResponse implements CommandResponse {

  private int statusCode = 0;
  private byte[] content = null;
  private String responseString = null;
  private String contentType = "";
  
  public ServiceProxyCommandResponse(int statusCode, byte[] content, String contentType) {
    this.statusCode = statusCode;
    this.content = content;
    this.contentType = contentType;
  }
  
  public ServiceProxyCommandResponse(int statusCode, String contentString, String contentType) {
    this.statusCode = statusCode;
    this.contentType = contentType;
    this.responseString = contentString;
  }
    
  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String getContentType() {
    return contentType;    
  }

  @Override
  public String getResponseString() {
    if (content == null) {
      return responseString;
    } else {
      try {
        return new String(content,"UTF-8");
      } catch (UnsupportedEncodingException e) {      
        e.printStackTrace();
        return "<applicationerror><error>unsupported encoding</error></applicationerror>";
      }
    }
  }

  @Override
  public byte[] getBytes() {    
    return content;
  }

  @Override
  public boolean isBinary() {
    // TODO Auto-generated method stub
    return false;
  }

}
