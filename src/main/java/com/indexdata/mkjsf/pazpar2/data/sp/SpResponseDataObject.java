package com.indexdata.mkjsf.pazpar2.data.sp;

import com.indexdata.mkjsf.pazpar2.data.ResponseDataObject;

/**
 * Base class for Service Proxy-only response data
 * 
 * @author Niels Erik
 *
 */
public class SpResponseDataObject extends ResponseDataObject {

  private static final long serialVersionUID = -3098556883153269199L;  

  /**
   * <p>Returns true if the command was not recognized by the Service Proxy,
   * passed on to Pazpar2, and then also not recognized by Pazpar2.</p> 
   * <p>This results in an error no 3 from Pazpar2 which is passed back through 
   * the Service Proxy.</p> 
   * <p>This would normally be caused by issuing a Service Proxy-only command that 
   * the given Service Proxy is not configured for, or which the given 
   * Service Proxy has mapped to a different command name - in both cases it will 
   * just fall through.</p> 
   *  
   * @return
   */
  public boolean unsupportedCommand() {
    if (hasServiceError() 
        && getServiceError().getCode().equals("3")
        && getServiceError().getValue().equals("command")) {        
        return true;
    }
    return false;
  }
  
}
