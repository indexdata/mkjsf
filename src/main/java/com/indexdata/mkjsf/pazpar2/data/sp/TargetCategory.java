package com.indexdata.mkjsf.pazpar2.data.sp;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

/**
 * Data from the <code>categories</code> command, can be accessed by <code>pzresp.sp.categories.targetCategories</code>
 * 
 * @author Niels Erik
 *
 */
public class TargetCategory extends ResponseDataObject {

  private static final long serialVersionUID = -3027515807117682584L;

  public String getCategoryName () {
    return getOneValue("categoryName");
  }
  
  public String getCategoryId() {
    return getOneValue("categoryId");
  }
   
}
