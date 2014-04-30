package com.indexdata.mkjsf.pazpar2.data;

public class InfoResponse extends ResponseDataObject {

  /**
   * 
   */
  private static final long serialVersionUID = 3084509436017631123L;

  public String getVersionPazpar2 () {
    return ((Pazpar2VersionResponse) getOneElement("version")).getPazpar2();
  }
  
  public String getVersionYaz() {
    return ((Pazpar2VersionResponse) getOneElement("version")).getYaz();
  }
  
  public Pazpar2VersionResponse getVersion() {
    return (Pazpar2VersionResponse) getOneElement("version");
  }
  
  
}
