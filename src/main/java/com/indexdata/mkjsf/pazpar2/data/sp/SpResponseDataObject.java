package com.indexdata.mkjsf.pazpar2.data.sp;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

public class SpResponseDataObject extends ResponseDataObject {

  private static final long serialVersionUID = -3098556883153269199L;
  private static Logger logger = Logger.getLogger(SpResponseDataObject.class);

  public boolean unsupportedCommand() {
    if (hasServiceError() && getServiceError().getCode().equalsIgnoreCase("3")) {
        logger.warn("The " + getType() + " command not supported by this Service Proxy (request fell through SP to Pazpar2).");
        return true;
    }
    return false;
  }
  
}
