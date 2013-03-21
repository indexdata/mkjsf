package com.indexdata.pz2utils4jsf.pazpar2;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.indexdata.pz2utils4jsf.config.ConfigurationReader;
import com.indexdata.pz2utils4jsf.pazpar2.sp.ServiceProxyClient;
import com.indexdata.pz2utils4jsf.pazpar2.sp.ServiceProxyInterface;
import com.indexdata.pz2utils4jsf.pazpar2.sp.ServiceProxySession;
import com.indexdata.pz2utils4jsf.pazpar2.sp.auth.ServiceProxyUser;
import com.indexdata.pz2utils4jsf.utils.Utils;

@Named("pz2") @SessionScoped @Alternative
public class Pz2ProxyBean extends Pz2Bean implements ServiceProxyInterface {
    
  private static final long serialVersionUID = 4221824985678758225L;
  private static Logger logger = Logger.getLogger(Pz2ProxyBean.class);
    
  @Inject ConfigurationReader configurator;
  @Inject ServiceProxyUser user;
  
  public Pz2ProxyBean() {
  }
  
  @PostConstruct
  public void initiatePz2Session() {
    logger.debug(Utils.objectId(this) + " will instantiate a Pz2Session next.");
    pz2 = new ServiceProxySession();
    searchClient = new ServiceProxyClient();
    logger.info("Using [" + Utils.objectId(searchClient) + "] configured by [" 
                          + Utils.objectId(configurator) + "] on session [" 
                          + Utils.objectId(pz2) + "]" );    
    pz2.init(searchClient,configurator);
  }


  public String login(String navigateTo) {
    logger.info("doing login");
    ((ServiceProxySession) pz2).setUser(user);
    return ((ServiceProxySession)pz2).login(navigateTo);
  }

}
