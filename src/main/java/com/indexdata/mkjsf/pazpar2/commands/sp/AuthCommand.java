package com.indexdata.mkjsf.pazpar2.commands.sp;

import com.indexdata.mkjsf.pazpar2.commands.CommandParameter;
import com.indexdata.mkjsf.pazpar2.commands.Pazpar2Command;

public class AuthCommand extends Pazpar2Command implements ServiceProxyCommand {

  private static final long serialVersionUID = 5487611235664162578L;

  public AuthCommand() {
    super("auth");
  }
  
  public void setAction (String action) {
    setParameterInState(new CommandParameter("action","=",action));
  }  
  
  public String getAction () {
    return getParameterValue("action");
  }
  
  public void setUsername(String username) {
    setParameterInState(new CommandParameter("username","=",username));
  }
  
  public String getUsername () {
    return getParameterValue("username");
  }
  
  public void setPassword (String password) {
    setParameterInState(new CommandParameter("password","=",password));
  }
  
  public String getPassword () {
    return getParameterValue("password");
  }
    
  public AuthCommand copy () {
    AuthCommand newCommand = new AuthCommand();
    for (String parameterName : parameters.keySet()) {
      newCommand.setParameterInState(parameters.get(parameterName).copy());      
    }    
    return newCommand;
  }

  @Override
  public ServiceProxyCommand getSp() {
    return this;
  }

  @Override
  public boolean spOnly() {
    return true;
  }
}
