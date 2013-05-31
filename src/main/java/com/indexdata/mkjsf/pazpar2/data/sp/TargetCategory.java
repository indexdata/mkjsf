package com.indexdata.mkjsf.pazpar2.data.sp;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

public class TargetCategory extends ResponseDataObject {

  private static final long serialVersionUID = -3027515807117682584L;

  public String getCategoryName () {
    return getOneValue("categoryName");
  }
  
  public String getCategoryId() {
    return getOneValue("categoryId");
  }
   
}
