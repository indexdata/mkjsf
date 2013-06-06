package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.state.StateManager;
import com.indexdata.mkjsf.utils.Utils;

/**
 * ServiceProxyCommands holds references to all commands that are 
 * Service Proxy-only, those that are NOT supported by straight Pazpar2, that is.
 * 
 * @author Niels Erik
 *
 */
public class ServiceProxyCommands implements Serializable {

  public static final String AUTH = "auth";
  public static final String CATEGORIES = "categories";
  private static final long serialVersionUID = 6223527018096841188L;
  private static Logger logger = Logger.getLogger(ServiceProxyCommands.class);
  private StateManager stateMgr = null; 
  
  public ServiceProxyCommands(StateManager stateMgr) {
    logger.info("Initializing ServiceProxyCommands [" + Utils.objectId(this) + "]");
    this.stateMgr = stateMgr;
  }
  
  /**
   * auth command - referenced from UI as <code>pzreq.sp.auth</code>
   * 
   * @return auth command from current state
   */
  public AuthCommand getAuth() {
    return (AuthCommand) (stateMgr.getCommand(AUTH));
  }
  
  /**
   * categories command - referenced from UI as <code>pzreq.sp.categories</code>
   * 
   * @return categories command from current state
   */  
  public CategoriesCommand getCategories() {
    return (CategoriesCommand) (stateMgr.getCommand(CATEGORIES));
  }


}
