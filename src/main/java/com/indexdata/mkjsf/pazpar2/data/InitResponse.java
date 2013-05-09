package com.indexdata.mkjsf.pazpar2.data;

public class InitResponse extends ResponseDataObject {

  private static final long serialVersionUID = -1479775157276901600L;

  public String getStatus() {
    return getOneElementValue("status");
  }

}
