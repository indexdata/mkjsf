package com.indexdata.mkjsf.pazpar2.data;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.mkjsf.pazpar2.data.Hit;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

public class ShowResponse extends ResponseDataObject {

  private static final long serialVersionUID = 7103554232106330370L;
  

  public String getStatus() {
    return getOneValue("status");
  }
  
  public String getActiveClients () {
    return getOneValue("activeclients");
  }
  
  public int getMerged () {
    return getIntValue("merged");
  }
  
  public String getTotal () {
    return getOneValue("total");    
  }
  
  public int getStart () {
    return getIntValue("start");
  }

  public int getNum () {
    return getIntValue("num");
  }
 
  public List<Hit> getHits() {
    List<Hit> hits = new ArrayList<Hit>();
    if (getElements("hit") != null) {
      for (ResponseDataObject element : getElements("hit")) {
        hits.add((Hit)element);
      }
    } 
    return hits;
  }
  

}
