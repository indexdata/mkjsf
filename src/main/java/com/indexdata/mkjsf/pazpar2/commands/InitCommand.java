package com.indexdata.mkjsf.pazpar2.commands;

import com.indexdata.mkjsf.pazpar2.commands.sp.ServiceProxyCommand;

public class InitCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = -4915976465898889987L;
  
  public InitCommand() {
    super("init");
  }
  
  public void setClear(String clear) { 
    setParameterInState(new CommandParameter("clear","=",clear));
  }
  
  public String getClear() {
    return getParameterValue("clear");
  }
  
  public void setService(String serviceId) {    
    setParameterInState(new CommandParameter("service","=",serviceId));
  }
  
  public String getService() {
    return getParameterValue("service");
  }
  
  @Override
  public void setSession (String sessionId) {
    throw new UnsupportedOperationException("Cannot set session id on init command");
  }
  
  @Override
  public String getSession () {
    throw new UnsupportedOperationException("Cannot set or get session id on init command");
  }
  
  public InitCommand copy () {
    InitCommand newCommand = new InitCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }
  
  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return false;
  }


}
