package com.indexdata.mkjsf.pazpar2.data;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

/**
 * Data from the <code>bytarget</code> command, child object of ByTargetResponse
 * 
 * @author Niels Erik
 *
 */
public class Target extends ResponseDataObject {

  private static final long serialVersionUID = 3343881183545520108L;

  public String getId () {
    return getOneValue("id");
  }
  
  public String getName() {
    return getOneValue("name");
  }
  
  public String getHits() {
    return getOneValue("hits");
  }
  
  public String getDiagnostic() {
    return getOneValue("diagnostic");
  }
  
  public String getRecords() {
    return getOneValue("records");
  }
  
  public String getState () {
    return getOneValue("state");
  }
    
}
