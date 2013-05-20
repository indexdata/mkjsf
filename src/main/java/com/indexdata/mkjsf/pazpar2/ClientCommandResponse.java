package com.indexdata.mkjsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import com.indexdata.masterkey.pazpar2.client.Pazpar2HttpResponse;

public class ClientCommandResponse implements HttpResponseWrapper {
    
  private int statusCode;
  private String contentType;
  private byte[] content = null;
  private String contentString = null;
  private byte[] bytesForParsing = null;
  
  public ClientCommandResponse(Pazpar2HttpResponse pz2response, ByteArrayOutputStream content) {    
    this.content = content.toByteArray();
    this.statusCode = pz2response.getStatusCode();
    this.contentType = pz2response.getContentType();
  }
    
  public ClientCommandResponse(int statusCode, String content, String contentType) {
    this.statusCode = statusCode;
    this.contentString = content;
    this.contentType = contentType;
  }
  
  public ClientCommandResponse(int statusCode, byte[] content, String contentType) {
    this.statusCode = statusCode;
    this.content = content;
    this.contentType = contentType;
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
      return contentString;
    } else if (isBinary()) {
      return "[binary response]";
    } else {
      try {
        return new String(content,"UTF-8");
      } catch (UnsupportedEncodingException e) {      
        e.printStackTrace();
        return "<error>unsupported encoding</error>";
      }
    }
  }

  @Override
  public byte[] getBytes() {
    return content;
  }
  
  public void setResponseToParse(String parseString) {    
    try {
      this.bytesForParsing = parseString.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {      
      e.printStackTrace();
    }
  }

  public byte[] getResponseToParse() {
    if (bytesForParsing != null) {
      return bytesForParsing;
    } else if (content != null) {
      return content;
    } else {
      try {
        return contentString.getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        return null;
      }
    }
  }
  
  @Override
  public boolean isBinary() {    
    return !contentType.contains("xml");
  }

}
