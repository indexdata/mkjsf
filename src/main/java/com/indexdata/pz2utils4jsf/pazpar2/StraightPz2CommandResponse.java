package com.indexdata.pz2utils4jsf.pazpar2;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import com.indexdata.masterkey.pazpar2.client.Pazpar2HttpResponse;

public class StraightPz2CommandResponse implements CommandResponse {
  
  private Pazpar2HttpResponse pz2httpResponse = null;
  private ByteArrayOutputStream content = null;
  
  public StraightPz2CommandResponse(Pazpar2HttpResponse pz2response, ByteArrayOutputStream content) {
    pz2httpResponse = pz2response;
    this.content = content;
  }

  @Override
  public int getStatusCode() {    
    return pz2httpResponse.getStatusCode();
  }

  @Override
  public String getContentType() {
    return pz2httpResponse.getContentType();
  }

  @Override
  public String getResponseString() {
    try {
      return content.toString("UTF-8");
    } catch (UnsupportedEncodingException e) {      
      e.printStackTrace();
      return null;
    }
  }


}
