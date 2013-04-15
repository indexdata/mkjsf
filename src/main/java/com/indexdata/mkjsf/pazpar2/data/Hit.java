package com.indexdata.mkjsf.pazpar2.data;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.mkjsf.pazpar2.data.Location;
import com.indexdata.mkjsf.pazpar2.data.Pazpar2ResponseData;

public class Hit extends Pazpar2ResponseData {

  
  private static final long serialVersionUID = 9039281987691623220L;

  public List<Location> getLocations() {
    List<Location> locations = new ArrayList<Location>();
    for (Pazpar2ResponseData element : getElements("location")) {
      locations.add((Location)element);
    }
    return locations;
  }
  
  public String getTitle () {
    return getOneElementValue("md-title");
  }
  
  public String getTitleRemainder() {
    return getOneElementValue("md-title-remainder");
  }
  
  public String getAuthor (String prefix) {
    return getOneElement("md-author") != null ? prefix + getOneElement("md-author").getValue() : "";
  }
  
  public String getAuthor () {
    return getOneElementValue("md-author");
  }
  
  public String getTitleResponsibility() {
    return getOneElementValue("md-title-responsibility");
  }
  
  public String getRecId() {
    return getOneElementValue("recid");
  }

  
}
