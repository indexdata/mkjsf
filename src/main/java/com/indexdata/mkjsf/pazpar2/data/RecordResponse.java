package com.indexdata.mkjsf.pazpar2.data;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.mkjsf.pazpar2.data.Location;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

public class RecordResponse extends ResponseDataObject {

  private static final long serialVersionUID = 6682722004285796002L;

  public String getRecId () {
    return getOneElementValue("recid");
  }
  
  public List<Location> getLocations() {
    List<Location> locations = new ArrayList<Location>();
    if (getElements("location")!=null) {      
      int i = 0;
      for (ResponseDataObject element : getElements("location")) {
        ((Location)element).setSequenceNumber(i++);
        locations.add((Location)element);
      }
    } else {
      logger.debug("Found no locations");
    }
    return locations;
  }

  public String getTitle() {
    return getOneElementValue("md-title");
  }
  
  public String getDate() {
    return getOneElementValue("md-date");
  }
  
  public String getAuthor() {
    return getOneElementValue("md-author");
  }
  
  public String getSubject() {
    return getOneElementValue("md-subject");
  }
  
  public String getSubjects() {
    StringBuilder builder = new StringBuilder("");
    for (ResponseDataObject data : getElements("md-subject")) {
      if (builder.length()==0) {
        builder.append(data.getValue());
      } else {
        builder.append(", ");
        builder.append(data.getValue());
      }
    }
    return builder.toString();
  }
    
  public Location getFirstLocation () {
    return getLocations().size()>0 ? getLocations().get(0) : null;
  }
  
  public String getActiveClients () {
    String activeclients = getOneElementValue("activeclients");
    logger.info("Request to get activeclients on record [" + getRecId() + "]. Is [" + activeclients + "]");    
    return activeclients;
  }
   
}
