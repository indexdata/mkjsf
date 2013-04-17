package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.state.StateManager;

public class InitCommand extends Pazpar2Command {

  private static final long serialVersionUID = -4915976465898889987L;

  public InitCommand(StateManager stateMgr) {
    super("init",stateMgr);
  }
  
  public void setClear(String clear) { 
    setParameterInState(new CommandParameter("clear","=",clear));
  }
  
  public void setService(String serviceId) {    
    setParameterInState(new CommandParameter("service","=",serviceId));
  }
  
  @Override
  public void setSession (String sessionId) {
    throw new UnsupportedOperationException("Cannot set session id on init command");
  }
  
  @Override
  public String getSession () {
    throw new UnsupportedOperationException("Cannot set or get session id on init command");
  }

}
