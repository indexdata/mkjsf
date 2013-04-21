package com.indexdata.mkjsf.pazpar2.data;

import java.util.HashMap;
import java.util.Map;

import com.indexdata.mkjsf.pazpar2.data.Pazpar2ResponseData;
import com.indexdata.mkjsf.pazpar2.data.TermListResponse;

public class TermListsResponse extends Pazpar2ResponseData {

  private static final long serialVersionUID = -1370643625715834978L;
  private int activeClients = -1;
  private Map<String,TermListResponse> termLists = new HashMap<String,TermListResponse>(); 
  
  public int getActiveClients() {
    return activeClients;
  }
  public void setActiveClients(int activeClients) {
    this.activeClients = activeClients;
  }

  public void addTermList(TermListResponse termList) {    
    this.termLists.put(termList.getName(),termList);
  }
  public TermListResponse getTermList(String name) {
    if (termLists.get(name)==null) {
      return new TermListResponse();
    } else {
      return termLists.get(name);
    }
  }
  
  
}
