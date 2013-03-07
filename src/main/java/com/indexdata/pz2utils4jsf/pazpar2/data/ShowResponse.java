package com.indexdata.pz2utils4jsf.pazpar2.data;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.pz2utils4jsf.pazpar2.data.Hit;
import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseData;

public class ShowResponse extends Pazpar2ResponseData {

  private static final long serialVersionUID = 7103554232106330370L;
  

  public String getStatus() {
    return getOneElementValue("status");
  }
  
  public String getActiveClients () {
    return getOneElementValue("activeclients");
  }
  
  public int getMerged () {
    return getIntValue("merged");
  }
  
  public String getTotal () {
    return getOneElementValue("total");    
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
      for (Pazpar2ResponseData element : getElements("hit")) {
        hits.add((Hit)element);
      }
    } 
    return hits;
  }
  

}
