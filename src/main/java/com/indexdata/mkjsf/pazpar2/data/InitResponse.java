package com.indexdata.mkjsf.pazpar2.data;

/**
 * Data from the <code>init</code> command, can be accessed by <code>pzresp.init</code>
 * 
 * @author Niels Erik
 *
 */
public class InitResponse extends ResponseDataObject {

  private static final long serialVersionUID = -1479775157276901600L;

  public String getStatus() {
    return getOneValue("status");
  }

}
