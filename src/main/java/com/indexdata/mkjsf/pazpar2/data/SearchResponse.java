package com.indexdata.mkjsf.pazpar2.data;

public class SearchResponse extends ResponseDataObject {

  private static final long serialVersionUID = -3320013021497018972L;
  private boolean isNew = true;
  
  public String getStatus() {
    return getOneElementValue("status");
  }
      
  public boolean isNew () {
    return isNew; 
  }
  
  public void setIsNew (boolean isNew) {
    this.isNew = isNew;
  }

}
