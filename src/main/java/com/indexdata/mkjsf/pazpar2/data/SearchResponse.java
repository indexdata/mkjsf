package com.indexdata.mkjsf.pazpar2.data;

/**
 * Data from the <code>search</code> command (a status message), can be accessed by <code>pzresp.search</code>
 * 
 * @author Niels Erik
 *
 */
public class SearchResponse extends ResponseDataObject {

  private static final long serialVersionUID = -3320013021497018972L;
  private boolean isNew = true;
  
  public String getStatus() {
    return getOneValue("status");
  }
      
  public boolean isNew () {
    return isNew; 
  }
  
  public void setIsNew (boolean isNew) {
    this.isNew = isNew;
  }

}
