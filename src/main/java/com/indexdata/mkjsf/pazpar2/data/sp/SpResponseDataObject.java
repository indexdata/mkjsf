package com.indexdata.mkjsf.pazpar2.data.sp;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

public class SpResponseDataObject extends ResponseDataObject {

  private static final long serialVersionUID = -3098556883153269199L;  

  public boolean unsupportedCommand() {
    if (hasServiceError() 
        && getServiceError().getCode().equals("3")
        && getServiceError().getValue().equals("command")) {        
        return true;
    }
    return false;
  }
  
}
