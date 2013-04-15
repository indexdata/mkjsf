package com.indexdata.mkjsf.pazpar2;

public interface CommandResponse {
  public int getStatusCode();
  public String getContentType();
  public String getResponseString();
}
