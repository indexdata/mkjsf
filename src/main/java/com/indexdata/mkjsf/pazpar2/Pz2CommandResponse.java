package com.indexdata.mkjsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import com.indexdata.masterkey.pazpar2.client.Pazpar2HttpResponse;

public class Pz2CommandResponse implements CommandResponse {
  
  private Pazpar2HttpResponse pz2httpResponse = null;
  private int statusCode;
  private String contentType;
  private byte[] content = null;
  private String contentString = null;
  
  public Pz2CommandResponse(Pazpar2HttpResponse pz2response, ByteArrayOutputStream content) {
    pz2httpResponse = pz2response;
    this.content = content.toByteArray();
    this.statusCode = pz2httpResponse.getStatusCode();
    this.contentType = pz2httpResponse.getContentType();
  }
  
  public Pz2CommandResponse(Pazpar2HttpResponse pz2response, String content) {
    pz2httpResponse = pz2response;
    this.contentString = content;
  }
  
  public Pz2CommandResponse(int statusCode, String content, String contentType) {
    this.statusCode = statusCode;
    this.contentString = content;
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
  public byte[] getBinaryResponse() {
    return content;
  }

  @Override
  public boolean isBinary() {    
    return !contentType.contains("xml");
  }

}
