package com.indexdata.mkjsf.pazpar2.data;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.mkjsf.pazpar2.data.Location;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

/**
 * Data from the <code>record</code> command, can be accessed by <code>pzresp.record</code>
 * 
 * @author Niels Erik
 *
 */
public class RecordResponse extends ResponseDataObject {

  private static final long serialVersionUID = 6682722004285796002L;

  public String getRecId () {
    return getOneValue("recid");
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
      logger.trace("Found no locations");
    }
    return locations;
  }

  public String getTitle() {
    return getOneValue("md-title");
  }
  
  public String getDate() {
    return getOneValue("md-date");
  }
  
  public String getAuthor() {
    return getOneValue("md-author");
  }
  
  public String getSubject() {
    return getOneValue("md-subject");
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
    if (getIsBinary()) {
      return "0";
    } else {
      String activeclients = getOneValue("activeclients");
      if (activeclients == null || activeclients.length()==0) {
        // Look for override
        activeclients = getAttribute("activeclients");
      }
      logger.info("Request to get activeclients on record [" + getRecId() + "]. Is [" + activeclients + "]");    
      return activeclients;
    }
  }
   
}
