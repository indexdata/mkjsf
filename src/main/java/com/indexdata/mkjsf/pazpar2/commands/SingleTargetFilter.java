package com.indexdata.mkjsf.pazpar2.commands;

import java.io.Serializable;

import com.indexdata.mkjsf.pazpar2.commands.SingleTargetFilter;

public class SingleTargetFilter implements Serializable {

  private static final long serialVersionUID = 2389085467202526537L;

  private String targetName = "";
  private String targetId = "";
  
  public SingleTargetFilter (String targetId, String targetName) {
    this.targetId = targetId;
    this.targetName = targetName;
  }
  
  public String getTargetName () {
    return targetName;
  }
  
  public String getTargetId () {
    return targetId;    
  }
  
  public String getFilterExpression () {
    return "pz:id="+targetId;
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof SingleTargetFilter) {
      return targetName.equals(((SingleTargetFilter) o).getTargetName()) && 
             targetId.equals(((SingleTargetFilter) o).getTargetId());
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode () {
    return (targetId+targetName).hashCode();
  }
  
  @Override
  public String toString () {
    return targetName + " (" + targetId + ")";
  }
  
}
