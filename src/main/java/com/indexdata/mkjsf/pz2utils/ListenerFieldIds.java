package com.indexdata.mkjsf.pz2utils;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named("pz2watch")
@ApplicationScoped
public class ListenerFieldIds implements Serializable {
  
  private static final long serialVersionUID = -57079241763914538L;
      
  public String getHistory () {
    return ":pz2watch:stateForm:windowlocationhash";
  }
    
  public String getActiveclients () {
    return ":pz2watch:activeclientsForm:activeclientsField";
  }
  
  public String getActiveclientsRecord () {
    return ":pz2watch:activeclientsForm:activeclientsFieldRecord";
  }
  
  public String getErrorMessages () {
    return ":pz2watch:activeclientsForm:errorMessages";
  }

}
