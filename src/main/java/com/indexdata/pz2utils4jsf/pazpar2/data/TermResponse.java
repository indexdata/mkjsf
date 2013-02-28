package com.indexdata.pz2utils4jsf.pazpar2.data;

import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseData;

public class TermResponse extends Pazpar2ResponseData {

  private static final long serialVersionUID = -8323959763575180678L;
  
  protected int frequency = -1;
  
  public String getName() {
    return getProperty("name");
  }
  public int getFrequency() {
    return Integer.parseInt(getProperty("frequency"));
  }
  
  public String toString() {
    return getProperty("name");
  }
  
}
