package com.indexdata.pz2utils4jsf.pazpar2.data;

import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseData;

public class Target extends Pazpar2ResponseData {

  private static final long serialVersionUID = 3343881183545520108L;

  public String getId () {
    return getOneElementValue("id");
  }
  
  public String getName() {
    return getOneElementValue("name");
  }
  
  public String getHits() {
    return getOneElementValue("hits");
  }
  
  public String getDiagnostic() {
    return getOneElementValue("diagnostic");
  }
  
  public String getRecords() {
    return getOneElementValue("records");
  }
  
  public String getState () {
    return getOneElementValue("state");
  }
    
}
