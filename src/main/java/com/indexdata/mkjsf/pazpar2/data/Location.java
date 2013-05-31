package com.indexdata.mkjsf.pazpar2.data;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;


public class Location extends ResponseDataObject {
    
  private static final long serialVersionUID = -1386527442857478225L;
  private int seqno = -1;
  
  public String getId() {
    return getAttribute("id");
  }
  
  public String getChecksum() {
    logger.debug("Request to get checksum");
    return getAttribute("checksum"); 
  }
    
  public String getName () {
    return getAttribute("name");
  }
  
  public String getSubject() {
    return getOneValue("md-subject");
  }
  
  public void setSequenceNumber(int num) {
    seqno = num;
  }
  
  public int getSequenceNumber () {
    return seqno;
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

  public String getAuthor() {
    return getOneValue("md-author");
  }
  
  public String getAuthors() {
    StringBuilder builder = new StringBuilder("");
    if (getElements("md-author") != null) {
      for (ResponseDataObject data : getElements("md-author")) {
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
