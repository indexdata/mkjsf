package com.indexdata.mkjsf.pazpar2.data;

import com.indexdata.mkjsf.pazpar2.data.Pazpar2ResponseData;

public class StatResponse extends Pazpar2ResponseData {
  
  private static final long serialVersionUID = -6578979787689458761L;

  public int getHits() {    
    return getProperty("hits")==null ? 0 : Integer.parseInt(getProperty("hits"));
  }
  
  public int getClients () {
    return getIntValue("clients");
  }
  
  public int getActiveClients () {
    return getIntValue("activeclients");
  }

  public int getRecords () {
    return getIntValue("records");
  }
  
  public int getUnconnected() {
    return getIntValue("unconnected");
  }
  
  public int getConnecting() {
    return getIntValue("connecting");
  }
  
  public int getWorking() {
    return getIntValue("working");
  }
  
  public int getIdle() {
    return getIntValue("idle");
  }
  
  public int getFailed() {
    return getIntValue("failed");
  }
  
  public int getError() {
    return getIntValue("error");
  }
  
  public String getProgress() {
    return getOneElementValue("progress");
  }
  
}
