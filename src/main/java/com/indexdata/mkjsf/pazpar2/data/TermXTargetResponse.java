package com.indexdata.mkjsf.pazpar2.data;

import com.indexdata.mkjsf.pazpar2.data.TermResponse;

public class TermXTargetResponse extends TermResponse {

  private static final long serialVersionUID = 5201902652960804977L;
  
  public String getId() {
    return getOneElement("id").getValue();
  }
  public int getApproximation () {
    return Integer.parseInt(getOneElement("approximation").getValue());
  }
  public int getRecords () {
    return Integer.parseInt(getOneElement("records").getValue());
  }
  public int getFiltered () {
    return Integer.parseInt(getOneElement("filtered").getValue());
  }
  public String getState () {
    return getOneElement("state").getValue();
  }
  public String getDiagnostic () {
    return getOneElement("diagnostic").getValue();
  }

}
