package com.indexdata.pz2utils4jsf.pazpar2.data;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.pz2utils4jsf.pazpar2.data.Pazpar2ResponseData;
import com.indexdata.pz2utils4jsf.pazpar2.data.Target;

public class ByTarget extends Pazpar2ResponseData {

  private static final long serialVersionUID = 3960644950805644518L;
  
  public List<Target> getTargets() {
    List<Target> targets = new ArrayList<Target>();
    if (getElements("target") != null) {
      for (Pazpar2ResponseData element : getElements("target")) {
        targets.add((Target)element);
      }
    }
    return targets;
  }
}
