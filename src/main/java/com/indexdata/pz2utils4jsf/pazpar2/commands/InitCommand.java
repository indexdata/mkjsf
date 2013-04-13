package com.indexdata.pz2utils4jsf.pazpar2.commands;

import com.indexdata.pz2utils4jsf.pazpar2.state.StateManager;

public class InitCommand extends Pazpar2Command {

  private static final long serialVersionUID = -4915976465898889987L;

  public InitCommand(StateManager stateMgr) {
    super("init",stateMgr);
  }
  
  public void setClear(String clear) {    
  }
  
  public void setService(String serviceId) {    
  }

}
