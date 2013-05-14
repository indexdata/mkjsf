package com.indexdata.mkjsf.pazpar2.data.sp;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

@SessionScoped
public class CategoriesResponse extends ResponseDataObject {

  private static final long serialVersionUID = 5502182636437956412L;
    
  public List<TargetCategory> getTargetCategories() {
    List<TargetCategory> targetCategories = new ArrayList<TargetCategory>();    
    if (getElements("category") != null) {
      for (ResponseDataObject element : getElements("category")) {
        targetCategories.add((TargetCategory)element);
      }
    }
    return targetCategories;
  }

  
}
