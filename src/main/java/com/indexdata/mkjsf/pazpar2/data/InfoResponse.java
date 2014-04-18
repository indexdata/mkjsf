package com.indexdata.mkjsf.pazpar2.data;

public class InfoResponse extends ResponseDataObject {

  /**
   * 
   */
  private static final long serialVersionUID = 3084509436017631123L;

  public String getPazpar2Version () {
    return getOneElement("version").getOneValue("pazpar2");
  }
  
  public String getYazVersion() {
    return getOneElement("version").getOneValue("yaz");
  }
  
  
}
