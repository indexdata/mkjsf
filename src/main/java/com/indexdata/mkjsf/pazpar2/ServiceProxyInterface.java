package com.indexdata.mkjsf.pazpar2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public interface ServiceProxyInterface  {  
  public String login(String navigateTo);  
  public void setInitFileName (String fileName);  
  public String getInitFileName();
  public ClientCommandResponse postInit() throws UnsupportedEncodingException, IOException;
  public ClientCommandResponse postInit(byte[] initDoc, boolean includeDebug) throws UnsupportedEncodingException, IOException;
  public String getInitResponse();
}
