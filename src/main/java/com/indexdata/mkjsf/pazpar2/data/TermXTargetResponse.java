package com.indexdata.mkjsf.pazpar2.data;

import com.indexdata.mkjsf.pazpar2.data.TermResponse;

/**
 * Data from the <code>termlist</code> command, child object of TermListResponse
 * 
 * @author Niels Erik
 *
 */
public class TermXTargetResponse extends TermResponse {

  private static final long serialVersionUID = 5201902652960804977L;
  
  public String getId() {
    return getOneElement("id").getValue();
  }
  public int getApproximation () {
    return Integer.parseInt(getOneValue("approximation"));
  }
  public int getRecords () {
    return Integer.parseInt(getOneValue("records"));
  }
  public int getFiltered () {
    return Integer.parseInt(getOneValue("filtered"));
  }
  public String getState () {
    return getOneValue("state");
  }
  public String getDiagnostic () {
    return getOneValue("diagnostic");
  }

}
