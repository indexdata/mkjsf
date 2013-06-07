package com.indexdata.mkjsf.pazpar2;

public interface HttpResponseWrapper {
  public int getStatusCode();
  public String getContentType();
  public String getResponseString();
  public byte[] getBytes();
  public boolean isBinary();
}
