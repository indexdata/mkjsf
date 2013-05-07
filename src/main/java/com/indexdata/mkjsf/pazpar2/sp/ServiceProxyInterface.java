package com.indexdata.mkjsf.pazpar2.sp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.indexdata.mkjsf.pazpar2.Pz2Interface;

public interface ServiceProxyInterface extends Pz2Interface {  
  public String login(String navigateTo);  
  public void setInitFileName (String fileName);  
  public String getInitFileName();
  public ServiceProxyCommandResponse postInit() throws UnsupportedEncodingException, IOException;
  public ServiceProxyCommandResponse postInit(byte[] initDoc, boolean includeDebug) throws UnsupportedEncodingException, IOException;
  public String getInitResponse();
  public void setServiceProxyUrl(String url);
  public String getServiceProxyUrl();
}
