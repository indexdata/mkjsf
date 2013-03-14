package com.indexdata.pz2utils4jsf.pazpar2;

public interface CommandResponse {
  public int getStatusCode();
  public String getContentType();
  public String getResponseString();
}
