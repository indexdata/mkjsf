package com.indexdata.pz2utils4jsf.pazpar2.data;

import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseData;

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
  
  public String getUnconnected() {
    return getOneElementValue("unconnected");
  }
  
  public String getConnecting() {
    return getOneElementValue("connecting");
  }
  
  public String getWorking() {
    return getOneElementValue("working");
  }
  
  public String getIdle() {
    return getOneElementValue("idle");
  }
  
  public String getFailed() {
    return getOneElementValue("failed");
  }
  
  public String getError() {
    return getOneElementValue("error");
  }
  
  public String getProgress() {
    return getOneElementValue("progress");
  }
  
}
