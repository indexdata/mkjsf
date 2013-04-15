package com.indexdata.mkjsf.pazpar2.data;

import com.indexdata.mkjsf.pazpar2.data.Pazpar2ResponseData;


public class Location extends Pazpar2ResponseData {
    
  private static final long serialVersionUID = -1386527442857478225L;

  public String getId() {
    return getAttribute("id");
  }
  
  public String getName () {
    return getAttribute("name");
  }
  
  public String getSubject() {
    return getOneElementValue("md-subject");
  }
  
  public String getSubjects() {
    StringBuilder builder = new StringBuilder("");
    for (Pazpar2ResponseData data : getElements("md-subject")) {
      if (builder.length()==0) {
        builder.append(data.getValue());
      } else {
        builder.append(", ");
        builder.append(data.getValue());
      }
    }
    return builder.toString();
  }

  public String getAuthor() {
    return getOneElementValue("md-author");
  }
  
  public String getAuthors() {
    StringBuilder builder = new StringBuilder("");
    if (getElements("md-author") != null) {
      for (Pazpar2ResponseData data : getElements("md-author")) {
        if (builder.length()==0) {
          builder.append(data.getValue());
        } else {
          builder.append(", ");
          builder.append(data.getValue());
        }
      }
    }
    return builder.toString();
  }
  
}
