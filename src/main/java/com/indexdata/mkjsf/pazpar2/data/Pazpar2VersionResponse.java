package com.indexdata.mkjsf.pazpar2.data;

public class Pazpar2VersionResponse extends ResponseDataObject {

  /**
   * 
   */
  private static final long serialVersionUID = 8565086923105413965L;

  public String getPazpar2 () {    
    return getOneValue("pazpar2");
  }
  
  public String getYaz() {
    return getOneValue("yaz");
  }
}
