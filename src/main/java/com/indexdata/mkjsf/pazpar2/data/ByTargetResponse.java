package com.indexdata.mkjsf.pazpar2.data;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;
import com.indexdata.mkjsf.pazpar2.data.Target;

/**
 * Data from the <code>bytarget</code> command, can be accessed by <code>pzresp.byTarget</code>
 * 
 * @author Niels Erik
 *
 */
public class ByTargetResponse extends ResponseDataObject {

  private static final long serialVersionUID = 3960644950805644518L;
  
  public List<Target> getTargets() {
    List<Target> targets = new ArrayList<Target>();
    if (getElements("target") != null) {
      for (ResponseDataObject element : getElements("target")) {
        targets.add((Target)element);
      }
    }
    return targets;
  }
}
