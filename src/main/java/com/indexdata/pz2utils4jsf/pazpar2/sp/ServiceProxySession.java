package com.indexdata.pz2utils4jsf.pazpar2.sp;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.pazpar2.Pz2Session;
import com.indexdata.pz2utils4jsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.pz2utils4jsf.utils.Utils;

public class ServiceProxySession extends Pz2Session implements ServiceProxyInterface {

  private ServiceProxyUser user; 
  private static final long serialVersionUID = -5770410029361522854L;
  private static Logger logger = Logger.getLogger(ServiceProxySession.class);
  
  public ServiceProxySession() {
    logger.info("Instantiating pz2 session object [" + Utils.objectId(this) + "]");
  }
  
  public void setUser(ServiceProxyUser user) {
    this.user = user;
  }

  @Override
  public String login(String navigateTo) {
    if (((ServiceProxyClient)searchClient).authenticate(user)) {
      return navigateTo;
    } else {
      return null;
    }      
  }
  

}
