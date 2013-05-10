package com.indexdata.mkjsf.pazpar2.commands.sp;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.indexdata.mkjsf.pazpar2.state.StateManager;
import com.indexdata.mkjsf.utils.Utils;

public class ServiceProxyCommands implements Serializable {

  public static final String AUTH = "auth";
  private static final long serialVersionUID = 6223527018096841188L;
  private static Logger logger = Logger.getLogger(ServiceProxyCommands.class);
  private StateManager stateMgr = null; 
  
  public ServiceProxyCommands(StateManager stateMgr) {
    logger.info("Initializing ServiceProxyCommands [" + Utils.objectId(this) + "]");
    this.stateMgr = stateMgr;
  }

  
  public AuthCommand getAuth() {
    return (AuthCommand) (stateMgr.getCommand(AUTH));
  }


}
